package com.alberovalley.appyuda;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.radioactiveyak.location_best_practices.utils.LegacyLastLocationFinder;


public class AppyudaWidgetProvider extends AppWidgetProvider /*implements ReverseGeocodingListener */{
	public static String ACTION_WIDGET_CLICK ="ACTION_WIDGET_CLICK";
	
	Context _context;
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		_context = context;
		// Obtiene todos los ids
        ComponentName thisWidget = new ComponentName(context, AppyudaWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        // Este bucle se ejecuta para cada App Widget perteneciente a este provider
		for (int widgetId : allWidgetIds) {

          
          // Crea un Intent 
          Intent intent = new Intent(context, AppyudaWidgetProvider.class);
          // indica la acción para gestionar el click
          intent.setAction(ACTION_WIDGET_CLICK);
          intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
          // crea el Pending Intent con el intent que lleva la acción del click
          //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
          PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

          // Obtiene el layout para el App Widget y le asigna un on-click listener al botón
          RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
          views.setImageViewResource(R.id.widget_imagen_clicable, R.drawable.redbutton);
          views.setOnClickPendingIntent(R.id.widget_imagen_clicable, pendingIntent);
         
          // Le dice al AppWidgetManager que realice una actualización al widget actual
          appWidgetManager.updateAppWidget(widgetId, views);
        }
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
	 super.onReceive(context, intent);
	 _context = context;
	 final String action = intent.getAction();

	 Log.d("Appyuda","onReceive");
	 if (action.equalsIgnoreCase(ACTION_WIDGET_CLICK)){
		 Log.d("Appyuda","clicado");
		 Toast.makeText(_context, "Buscando su dirección actual", Toast.LENGTH_LONG).show();
		 /*
		 ReverseGeocodingAsynctask rga = new ReverseGeocodingAsynctask();
		 rga.setReverseGeocodingListener(this);
		 */
		 LegacyLastLocationFinder lf = new LegacyLastLocationFinder(context);
		 
		 
		 Location location = lf.getLastBestLocation(100, 10 *60*1000);
		 //rga.execute(new Double[]{location.getLatitude(), location.getLongitude()});
		 //rga.execute(new Double[]{40.36281462293118,-3.9140933845192194});
		 Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		 String direccion = "";
		 try {
			List<Address> addresses = geocoder.getFromLocation (location.getLatitude(), location.getLongitude(), 1);
			Address address = addresses.get(0);
			int i = 0;
			do{
				direccion = direccion.equalsIgnoreCase("")?address.getAddressLine(i):direccion + ", " + address.getAddressLine(i);
				i++;
			}while (address.getAddressLine(i)!= null);
			
		} catch (IOException e) {
			direccion = "problema obteniendo la dirección" + e.getMessage();
			Log.e("Appayuda", direccion);
			
		}
		Toast.makeText(context, direccion, Toast.LENGTH_LONG).show();
		 
	 }else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
		  
	  }
	 ComponentName thisWidget = new ComponentName(context, AppyudaWidgetProvider.class);
	 AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
	 int[] ids = appWidgetManager.getAppWidgetIds(thisWidget);
	 onUpdate(context, appWidgetManager, ids);
            	
	}
	
/*
	@Override
	public void onReverseGeocoding(ResponseEnvelope response) {
		String direccion;
		if (response.getStatusCode()== ResponseEnvelope.STATUS_OK){
			// obtenemos el DAO adecuado del ResponseEnvelope cuidando de hacer el casting correspondiente
			GoogleGeocodeAddressDAO dao = (GoogleGeocodeAddressDAO) response.getDao();
			//damos uso a los datos que contiene el dao
			direccion = dao.getFormattedAddress();
		}else{
			// gestionamos el caso de que el proceso terminara con errores
			Log.e("Appyuda","onReverseGeocoding "+ response.getErrMessage());
			direccion = "error: "+ response.getErrMessage();
		}
		
		Toast.makeText(_context, direccion, Toast.LENGTH_LONG).show();
		
	}*/
}
