package com.michalraq.proximitylightapp.data.estimote;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.api.ProximityObserver;
import com.estimote.proximity_sdk.api.ProximityObserverBuilder;
import com.estimote.proximity_sdk.api.ProximityZone;
import com.estimote.proximity_sdk.api.ProximityZoneBuilder;
import com.estimote.proximity_sdk.api.ProximityZoneContext;
import com.michalraq.proximitylightapp.service.Client;
import com.michalraq.proximitylightapp.view.ServiceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * Klasa odpowiadająca za sterowanie obserwatorem strefy nadajnika.
 */
public class ProximityContentManager {

    private Context context;
    private EstimoteCloudCredentials cloudCredentials;
    private ProximityObserver.Handler proximityObserverHandler;

    /**
     *
     * @param context kontekst.
     * @param cloudCredentials dane wygenerowane przez chmurę Estimote potrzebne do połączenia aplikacji z usługą.
     */
    public ProximityContentManager(Context context, EstimoteCloudCredentials cloudCredentials) {
        this.context = context;
        this.cloudCredentials = cloudCredentials;
    }

    /**
     * Funkcja uruchamiająca obserwatora. Wraz z definicją obsługiwanych eventów związanych z wejściem i wyjściem ze strefy.
     */
    public void start() {

        ProximityObserver proximityObserver = new ProximityObserverBuilder(context, cloudCredentials)
                .onError(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        Log.e("app", "proximity observer error: " + throwable);
                        return null;
                    }
                })
                .withBalancedPowerMode()
                .build();

        ProximityZone kuchnia = new ProximityZoneBuilder()
                .forTag("kuchnia")
                .inCustomRange(4.0)
                .onEnter(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext zoneContext) {
                        turnLight(zoneContext,1);
                        return null;
                    }
                }) .onExit(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext zoneContext) {
                        turnLight(zoneContext,0);

                        return null;
                    }
                }).build();
        ProximityZone salon = new ProximityZoneBuilder()
                .forTag("salon")
                .inCustomRange(4.0)
                .onEnter(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext zoneContext) {
                        turnLight(zoneContext,1);
                        return null;
                    }
                }) .onExit(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext zoneContext) {
                        turnLight(zoneContext,0);

                        return null;
                    }
                }).build();
        ProximityZone biuro = new ProximityZoneBuilder()
                .forTag("biuro")
                .inCustomRange(2.5)
                .onEnter(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext zoneContext) {
                        turnLight(zoneContext,1);
                        return null;
                    }
                }) .onExit(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext zoneContext) {
                   turnLight(zoneContext,0);
                        return null;
                    }
                }).build();
        proximityObserverHandler = proximityObserver.startObserving(kuchnia,salon,biuro);
    }

    /**
     * Funkcja, która przesyła wiadomosć do serwera, aby włączyć lub wyłączyć światło w zależności od sygnału podanego w parametrze.
     * @param zoneContext
     * @param signal przyjmujący wartość 0 lub 1
     */
    private void turnLight(ProximityZoneContext zoneContext, int signal){
        String place = zoneContext.getAttachments().get("place");
        if (place == null) {
            place = "unknown";
        }
        place = signal + place;

        if(signal==1) {
            Toast.makeText(context, "Włączam światło w " + place, Toast.LENGTH_LONG).show();
        }else
        {
            Toast.makeText(context, "Wyłączam światło w " + place, Toast.LENGTH_LONG).show();
        }

        if(ServiceManager.isServiceStarted)
            Client.sendMessage(place);

    }
    /**
     * Funckja zatrzymująca obserwatora.
     */
    public void stop() {
        proximityObserverHandler.stop();
    }
}
