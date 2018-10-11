package com.michalraq.proximitylightapp.estimote;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.api.ProximityObserver;
import com.estimote.proximity_sdk.api.ProximityObserverBuilder;
import com.estimote.proximity_sdk.api.ProximityZone;
import com.estimote.proximity_sdk.api.ProximityZoneBuilder;
import com.estimote.proximity_sdk.api.ProximityZoneContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class ProximityContentManager {

    private Context context;
    private ProximityContentAdapter proximityContentAdapter;
    private EstimoteCloudCredentials cloudCredentials;
    private ProximityObserver.Handler proximityObserverHandler;

    public ProximityContentManager(Context context, ProximityContentAdapter proximityContentAdapter, EstimoteCloudCredentials cloudCredentials) {
        this.context = context;
        this.proximityContentAdapter = proximityContentAdapter;
        this.cloudCredentials = cloudCredentials;
    }

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


        ProximityZone places = new ProximityZoneBuilder().forTag("proximity-light-4nu").inCustomRange(3.5)
                .onEnter(new Function1<ProximityZoneContext, Unit>() {
            @Override
            public Unit invoke(ProximityZoneContext context) {
                String place = context.getAttachments().get("place");
                Log.d("app", "Welcome to" + place );
                return null;
            }
        }) .onExit(new Function1<ProximityZoneContext, Unit>() {
            @Override
            public Unit invoke(ProximityZoneContext context) {
                Log.d("app", "Bye bye, come again!");
                return null;
            }
        }).build();



        ProximityZone zone = new ProximityZoneBuilder()
                .forTag("proximity-light-4nu")
                .inCustomRange(3.0)
                .onContextChange(new Function1<Set<? extends ProximityZoneContext>, Unit>() {
                    @Override
                    public Unit invoke(Set<? extends ProximityZoneContext> contexts) {

                        List<ProximityContent> nearbyContent = new ArrayList<>(contexts.size());

                        for (ProximityZoneContext proximityContext : contexts) {
                            String title = proximityContext.getAttachments().get("proximity-light-4nu/title");
                            if (title == null) {
                                title = "unknown";
                            }
                            String subtitle = Utils.getShortIdentifier(proximityContext.getDeviceId());

                            nearbyContent.add(new ProximityContent(title, subtitle));
                        }

                        proximityContentAdapter.setNearbyContent(nearbyContent);
                        proximityContentAdapter.notifyDataSetChanged();

                        return null;
                    }
                }).build();

        proximityObserverHandler = proximityObserver.startObserving(places);
    }

    public void stop() {
        proximityObserverHandler.stop();
    }
}
