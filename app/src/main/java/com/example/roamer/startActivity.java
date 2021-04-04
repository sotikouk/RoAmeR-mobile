package com.example.roamer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.roamer.agents.clientAgent;
import com.example.roamer.agents.proximityAgent;

import java.util.logging.Level;

import jade.android.AndroidHelper;
import jade.android.MicroRuntimeService;
import jade.android.MicroRuntimeServiceBinder;
import jade.android.RuntimeCallback;
import jade.core.MicroRuntime;
import jade.core.Profile;
import jade.util.Logger;
import jade.util.leap.Properties;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;


public class startActivity extends Activity {
    private Logger logger = Logger.getJADELogger(this.getClass().getName());
    private MicroRuntimeServiceBinder microRuntimeServiceBinder;
    private ServiceConnection serviceConnection;
    String clientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button button = findViewById((R.id.startButton));
        button.setOnClickListener(buttonARListener);

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }



    private final View.OnClickListener buttonARListener = new View.OnClickListener() {

        public void onClick(View v) {
            final EditText nameField = findViewById(R.id.PersonName);
            clientName = nameField.getText().toString();
            // Στην περίπτωση της εφαρμογής μας το hostName ειναι 162.168.1.5
            // Και το port 1099
                    String host = "192.168.1.6";
                    String port = "1099";

                    try {
                    startAR(clientName, host, port, agentStartupCallback);

                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Unexpected exception creating chat agent!");
                }


        }
    };


    private final RuntimeCallback<AgentController> agentStartupCallback = new RuntimeCallback<AgentController>() {
            @Override
            public void onSuccess(AgentController agent) {

            }

            @Override
            public void onFailure(Throwable throwable) {
            }
    };

    public void startAR(final String clientName, final String host, final String port,
                        final RuntimeCallback<AgentController> agentStartupCallback) {

        final Properties profile = new Properties();
        profile.setProperty(Profile.MAIN_HOST, host);
        profile.setProperty(Profile.MAIN_PORT, port);
        profile.setProperty(Profile.MAIN, Boolean.FALSE.toString());
        profile.setProperty(Profile.JVM, Profile.ANDROID);

        profile.setProperty(Profile.LOCAL_HOST, AndroidHelper.getLocalIPAddress());

        if (microRuntimeServiceBinder == null) {
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName className,
                                               IBinder service) {
                    microRuntimeServiceBinder = (MicroRuntimeServiceBinder) service;
                    logger.log(Level.INFO, "Gateway successfully bound to MicroRuntimeService");
                    startContainer(clientName, profile, agentStartupCallback);
                }

                public void onServiceDisconnected(ComponentName className) {
                    microRuntimeServiceBinder = null;
                    logger.log(Level.INFO, "Gateway unbound from MicroRuntimeService");
                }
            };
            logger.log(Level.INFO, "Binding Gateway to MicroRuntimeService...");
            bindService(new Intent(getApplicationContext(),
                            MicroRuntimeService.class), serviceConnection,
                    Context.BIND_AUTO_CREATE);
        } else {
            logger.log(Level.INFO, "MicroRuntimeGateway already binded to service");
            startContainer(clientName, profile, agentStartupCallback);
        }

    }

    private void startContainer(final String clientName, Properties profile,
                                final RuntimeCallback<AgentController> agentStartupCallback) {
        if (!MicroRuntime.isRunning()) {
            microRuntimeServiceBinder.startAgentContainer(profile,
                    new RuntimeCallback<Void>() {
                        @Override
                        public void onSuccess(Void thisIsNull) {
                            logger.log(Level.INFO, "Successfully start of the container...");
                            startAgent(clientName, agentStartupCallback);
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            logger.log(Level.SEVERE, "Failed to start the container...");
                        }
                    });
        } else {
            startAgent(clientName, agentStartupCallback);
        }
    }

    private void startAgent(final String clientName,
                            final RuntimeCallback<AgentController> agentStartupCallback) {
        microRuntimeServiceBinder.startAgent(clientName,
                clientAgent.class.getName(),
                new Object[] { getApplicationContext() },
                new RuntimeCallback<Void>() {
                    @Override
                    public void onSuccess(Void thisIsNull) {
                        logger.log(Level.INFO, "Successfully start of the "
                                + clientAgent.class.getName() + "...");
                        try {
                            agentStartupCallback.onSuccess(MicroRuntime
                                    .getAgent(clientName));
                        } catch (ControllerException e) {
                            // Should never happen
                            agentStartupCallback.onFailure(e);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        logger.log(Level.SEVERE, "Failed to start the "
                                + clientAgent.class.getName() + "...");
                        agentStartupCallback.onFailure(throwable);
                    }
                });
        final String proximityName;
        proximityName = clientName+"prx";

        microRuntimeServiceBinder.startAgent(proximityName,
                proximityAgent.class.getName(),
                new Object[] { getApplicationContext() },
                new RuntimeCallback<Void>() {
                    @Override
                    public void onSuccess(Void thisIsNull) {
                        logger.log(Level.INFO, "Successfully start of the "
                                + proximityAgent.class.getName() + "...");
                        try {
                            agentStartupCallback.onSuccess(MicroRuntime
                                    .getAgent(proximityName));
                        } catch (ControllerException e) {
                            // Should never happen
                            agentStartupCallback.onFailure(e);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        logger.log(Level.SEVERE, "Failed to start the "
                                + proximityAgent.class.getName() + "...");
                        agentStartupCallback.onFailure(throwable);
                    }
                });
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Name", proximityName);
        startActivity(intent);
    }

}
