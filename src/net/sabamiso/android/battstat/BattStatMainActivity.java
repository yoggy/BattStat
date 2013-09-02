package net.sabamiso.android.battstat;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

public class BattStatMainActivity extends Activity implements Runnable{

	private Handler handler = new Handler();
	BatteryBroadcastReceiver receiver;

	Item item_level;
	Item item_voltage;
	Item item_status;
	Item item_plugged_type;
	Item item_health;
	
	ItemAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_batt_stat_main);

		ListView listview = (ListView) findViewById(R.id.listView1);
		item_level = new Item("Battery Level", "");
		item_voltage = new Item("Voltage", "");
		item_status = new Item("Status", "");
		item_plugged_type = new Item("Plugged Type", "");
		item_health = new Item("Battery Health", "");

		ArrayList<Item> list = new ArrayList<Item>();
		list.add(item_level);
		list.add(item_voltage);
		list.add(item_status);
		list.add(item_plugged_type);
		list.add(item_health);

		adapter = new ItemAdapter(this, list);
		listview.setAdapter(adapter);

		receiver = new BatteryBroadcastReceiver(this);
	}

	@Override
	public void onResume() {
		super.onResume();

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(receiver, filter);
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
		finish();
	}

	@Override
	public void run() {
		item_level.setValue(Integer.toString(receiver.getLevel()) + " %");
		item_status.setValue(receiver.getStatus());
		item_voltage.setValue(Integer.toString(receiver.getVoltage()) + "mV");
		item_plugged_type.setValue(receiver.getPluggedType());
		item_health.setValue(receiver.getHealth());
		adapter.notifyDataSetInvalidated();
	}

	class BatteryBroadcastReceiver extends BroadcastReceiver {
		BattStatMainActivity activity;
		int level = 0;
		String status;
		String plugged_type = "";
		String health = "";
		int voltage = 0;

		public BatteryBroadcastReceiver(BattStatMainActivity activity) {
			this.activity = activity;
		}

		public int getLevel() {
			return level;
		}

		public String getStatus() {
			return status;
		}
		
		public String getPluggedType() {
			return plugged_type;
		}

		public String getHealth() {
			return health;
		}

		public int getVoltage() {
			return voltage;
		}
		
		@Override
		public void onReceive(Context context, Intent intent) {
			int lv = intent.getIntExtra("level", 0);
			int s = intent.getIntExtra("scale", 0);

			this.level = (int) (lv / (float) s * 100.0);

			this.voltage = intent.getIntExtra("voltage", 0);
			
            switch (intent.getIntExtra("status", 0)) {
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                status = "Unknown";
                break;
            case BatteryManager.BATTERY_STATUS_CHARGING:
            	status = "Charging";
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
            	status = "Discharging";
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
            	status = "Not Charging";
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
            	status = "Full";
                break;
            }
			
			
			switch (intent.getIntExtra("plugged", 0)) {
			case BatteryManager.BATTERY_PLUGGED_AC:
				plugged_type = "AC";
				break;
			case BatteryManager.BATTERY_PLUGGED_USB:
				plugged_type = "USB";
				break;
			case 4: // BatteryManager.BATTERY_PLUGGED_WIRELESS 
				plugged_type = "Wireless";
				break;
			default:
				plugged_type = "None";
				break;
			}

			switch (intent.getIntExtra("health", 0)) {
			case BatteryManager.BATTERY_HEALTH_UNKNOWN:
				health = "unknown";
				break;
			case BatteryManager.BATTERY_HEALTH_GOOD:
				health = "Good";
				break;
			case BatteryManager.BATTERY_HEALTH_OVERHEAT:
				health = "Overheat";
				break;
			case BatteryManager.BATTERY_HEALTH_DEAD:
				health = "Dead";
				break;
			case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
				health = "Over Voltage";
				break;
			case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
				health = "Unspecified Failure";
				break;
			}
			
			activity.handler.post(activity);
		}
	}

};
