package com.alberovalley.appyuda;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.alberovalley.googleApis.geocoding.asyntasks.ReverseGeocodingAsynctask;
import com.alberovalley.googleApis.geocoding.asyntasks.listeners.ReverseGeocodingListener;
import com.alberovalley.googleApis.geocoding.dao.GoogleGeocodeAddressDAO;
import com.alberovalley.googleApis.response.ResponseEnvelope;
import com.radioactiveyak.location_best_practices.utils.LegacyLastLocationFinder;


public class AppyudaWidgetProvider extends AppWidgetProvider implements ReverseGeocodingListener {
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
		 ReverseGeocodingAsynctask rga = new ReverseGeocodingAsynctask();
		 rga.setReverseGeocodingListener(this);
		 
		 LegacyLastLocationFinder lf = new LegacyLastLocationFinder(context);
		 
		 //Location location = getLastKnownLocation(context);
		 Location location = lf.getLastBestLocation(100, 10 *60*1000);
		 rga.execute(new Double[]{location.getLatitude(), location.getLongitude()});
		 
		 
	 }else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
		  
	  }
	 ComponentName thisWidget = new ComponentName(context, AppyudaWidgetProvider.class);
	 AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
	 int[] ids = appWidgetManager.getAppWidgetIds(thisWidget);
	 onUpdate(context, appWidgetManager, ids);
            	
	}
	
	public Location getLastKnownLocation(Context context) {
        // pillas el location manager a partir del contexto:
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // defines el criterio para seleccionar el provider de localización:
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // pillas el provider en función de ese criterio:
        String provider = locationManager.getBestProvider(criteria, true);
        // registrar esta clase como listener para recibir los cambios de posición:
        Location lastLocation = locationManager.getLastKnownLocation(provider);
        
        return lastLocation;
	}

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
			direccion = "error";
		}
		
		Toast.makeText(_context, direccion, Toast.LENGTH_LONG).show();
		
	}
}
