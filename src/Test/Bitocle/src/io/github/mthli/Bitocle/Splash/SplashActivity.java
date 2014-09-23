package io.github.mthli.Bitocle.Splash;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import io.github.mthli.Bitocle.Main.MainActivity;
import io.github.mthli.Bitocle.R;
import org.eclipse.egit.github.core.Authorization;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.OAuthService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends Activity {
    private SharedPreferences preferences;
    private String username;
    private String password;
    private ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        preferences = getSharedPreferences(getString(R.string.sp_file), MODE_PRIVATE);
        /*
        String oAuth = preferences.getString(getString(R.string.sp_oauth), null);
        if (oAuth != null) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra(getString(R.string.splash_to_main), false);
            startActivity(intent);
            finish();
        }
        */

        Button signIn = (Button) findViewById(R.id.splash_sign_in_button);
        Button signUp = (Button) findViewById(R.id.splash_sign_up_button);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignInDialog();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToGitHub();
            }
        });
    }

    private void showSignInDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);

        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(
                R.layout.splash_dialog,
                null
        );
        final EditText userText = (EditText) layout.findViewById(R.id.splash_sign_in_dialog_username);
        final EditText passText = (EditText) layout.findViewById(R.id.splash_sign_in_dialog_password);
        passText.setTypeface(Typeface.DEFAULT);
        passText.setTransformationMethod(new PasswordTransformationMethod());
        builder.setView(layout);

        builder.setPositiveButton(
                getString(R.string.splash_sign_in_dialog_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        username = userText.getText().toString();
                        password = passText.getText().toString();

                        if (username.length() == 0 || password.length() == 0) {
                            Toast.makeText(
                                    SplashActivity.this,
                                    R.string.splash_input_error,
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            progress = new ProgressDialog(SplashActivity.this);
                            progress.setMessage(
                                    getString(R.string.splash_sign_in_authenticating)
                            );
                            progress.setCancelable(false);
                            progress.show();

                            HandlerThread thread = new HandlerThread(
                                    getString(R.string.splash_sign_in_thread)
                            );
                            thread.start();
                            Handler handler = new Handler(thread.getLooper());
                            handler.post(signInThread);
                        }
                    }
                }
        );

        builder.setNegativeButton(
                getString(R.string.splash_sign_in_dialog_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /* Do nothing */
                    }
                }
        );

        builder.show();
    }

    private void jumpToGitHub() {
        Uri uri = Uri.parse(getString(R.string.splash_sign_up_uri));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    Runnable signInThread = new Runnable() {
        @Override
        public void run() {
            GitHubClient client = new GitHubClient();
            client.setCredentials(username, password);
            client.setUserAgent(getString(R.string.app_name));

            Authorization authorization = null;
            OAuthService service = new OAuthService(client);
            try {
                List<Authorization> list = service.getAuthorizations();
                for (Authorization a : list) {
                    if (getString(R.string.app_name).equals(a.getNote())) {
                        authorization = a;
                        break;
                    }
                }

                if (authorization == null) {
                    authorization = new Authorization();

                    authorization.setNote(getString(R.string.app_name));
                    authorization.setUrl(getString(R.string.app_url));
                    List<String> scopes = new ArrayList<String>();
                    scopes.add(getString(R.string.permission_notifications));
                    scopes.add(getString(R.string.permission_repo));
                    scopes.add(getString(R.string.permission_user));
                    authorization.setScopes(scopes);

                    authorization = service.createAuthorization(authorization);
                }

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(getString(R.string.sp_oauth), authorization.getToken());
                editor.putString(getString(R.string.sp_username), username);
                editor.putInt(getString(R.string.sp_highlight_num), 0);
                editor.putString(getString(R.string.sp_highlight_css), getString(R.string.default_css));
                editor.commit();
                progress.dismiss();

                /*
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra(getString(R.string.splash_to_main), true);
                startActivity(intent);
                finish();
                */
            } catch (IOException i) {
                progress.dismiss();
                Toast.makeText(
                        SplashActivity.this,
                        R.string.splash_sign_in_error,
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    };
}
