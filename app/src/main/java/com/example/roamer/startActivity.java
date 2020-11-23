package com.example.roamer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.roamer.agents.clientAgent;

import java.util.Properties;

import jade.android.AndroidHelper;
import jade.android.MicroRuntimeService;
import jade.android.MicroRuntimeServiceBinder;
import jade.android.RuntimeCallback;
import jade.core.MicroRuntime;
import jade.core.Profile;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

public class startActivity extends Activity {

    private MicroRuntimeServiceBinder microRuntimeServiceBinder;
    private ServiceConnection serviceConnection;

    private String clientName;
    EditText hostField;
    EditText portField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button button = (Button) findViewById((R.id.startButton));

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    private static boolean checkName(String clientName) {
        if (clientName == null || clientName.trim().equals("")){
            return false;
        }
        return true;
    }

    private View.OnClickListener buttonARListener = new View.OnClickListener() {
        @SuppressLint("WrongViewCast")
        @Override
        public void onClick(View v) {
            final EditText nameField = (EditText) findViewById(R.id.PersonName);
            clientName = nameField.getText().toString();
            if (!checkName(clientName)) {
                SharedPreferences settings = getSharedPreferences("jadeRoamerPrefs", 0);
                String host = settings.getString("defaultHost", "");
                String port = settings.getString("defaultPort", "");
                hostField = (EditText) findViewById(R.id.editHost);
                hostField.setText(host);
                portField = (EditText) findViewById(R.id.editPort);
                portField.setText(port);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("defaultHost", hostField.getText().toString());
                editor.putString("defaultPort", portField.getText().toString());
                startAR(clientName, host, port, agentStartupCallback);
            }
        }
    };
    private RuntimeCallback<AgentController> agentStartupCallback = new RuntimeCallback<AgentController>() {
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
                public void onServiceConnected(ComponentName className,
                                               IBinder service) {
                    microRuntimeServiceBinder = (MicroRuntimeServiceBinder) service;

                    startContainer(clientName, (jade.util.leap.Properties) profile, agentStartupCallback);
                };

                public void onServiceDisconnected(ComponentName className) {
                    microRuntimeServiceBinder = null;
                }
            };
            bindService(new Intent(getApplicationContext(),
                            MicroRuntimeService.class), serviceConnection,
                    Context.BIND_AUTO_CREATE);
        } else {
            startContainer(clientName, (jade.util.leap.Properties) profile, agentStartupCallback);
        }

    }

    private void startContainer(final String clientName, jade.util.leap.Properties profile,
                                final RuntimeCallback<AgentController> agentStartupCallback) {
        if (!MicroRuntime.isRunning()) {
            microRuntimeServiceBinder.startAgentContainer(profile,
                    new RuntimeCallback<Void>() {
                        @Override
                        public void onSuccess(Void thisIsNull) {
                            startAgent(clientName, agentStartupCallback);
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                        }
                    });
        } else {
            startAgent(clientName, agentStartupCallback);
        }
    }

    private void startAgent(final String nickname,
                            final RuntimeCallback<AgentController> agentStartupCallback) {
        microRuntimeServiceBinder.startAgent(nickname,
                clientAgent.class.getName(),
                new Object[] { getApplicationContext() },
                new RuntimeCallback<Void>() {
                    @Override
                    public void onSuccess(Void thisIsNull) {

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
                        agentStartupCallback.onFailure(throwable);
                    }
                });
    }

}
