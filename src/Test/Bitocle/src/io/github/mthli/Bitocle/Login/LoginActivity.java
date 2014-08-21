package io.github.mthli.Bitocle.Login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import io.github.mthli.Bitocle.Main.MainActivity;
import io.github.mthli.Bitocle.R;
import org.eclipse.egit.github.core.Authorization;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.OAuthService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String username;
    private String password;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        /*
         * 检测用户登陆状态
         *
         * 如果SharedPreferences中存在用户信息，
         * 则说明用户已经登陆，此时直接跳转到MainActivity即可；
         * 否则进入登陆界面
         */
        sharedPreferences = getSharedPreferences(getString(R.string.login_sp), MODE_PRIVATE);
        String oAuth = sharedPreferences.getString(getString(R.string.login_sp_oauth), null);
        if (oAuth != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra(getString(R.string.login_intent), false);
            startActivity(intent);
            finish();
        }

        final EditText userText = (EditText) findViewById(R.id.login_username);
        final EditText passText = (EditText) findViewById(R.id.login_password);
        /* 保持EditText字体的一致性 */
        passText.setTypeface(Typeface.DEFAULT);
        passText.setTransformationMethod(new PasswordTransformationMethod());
        Button button = (Button) findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = userText.getText().toString();
                password = passText.getText().toString();

                /* 给出相应的错误提示 */
                if (username.length() == 0 && password.length() == 0) {
                    SuperToast.create(
                            LoginActivity.this,
                            getString(R.string.login_message_miss_both),
                            SuperToast.Duration.SHORT,
                            Style.getStyle(Style.RED)
                    ).show();
                } else if (username.length() != 0 && password.length() == 0) {
                    SuperToast.create(
                            LoginActivity.this,
                            getString(R.string.login_message_miss_password),
                            SuperToast.Duration.SHORT,
                            Style.getStyle(Style.RED)
                    ).show();
                } else if (username.length() == 0 && password.length() != 0) {
                    SuperToast.create(
                            LoginActivity.this,
                            getString(R.string.login_message_miss_username),
                            SuperToast.Duration.SHORT,
                            Style.getStyle(Style.RED)
                    ).show();
                } else {
                    /* ProgressDialog显示当前正在运行的状态 */
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage(getString(R.string.login_message_authoring));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    /* 开启新的线程用于认证 */
                    HandlerThread handlerThread = new HandlerThread(getString(R.string.login_thread));
                    handlerThread.start();
                    Handler handler = new Handler(handlerThread.getLooper());
                    handler.post(authorizationThread);
                }
            }
        });

    }

    /* 用于认证的线程 */
    Runnable authorizationThread = new Runnable() {
        @Override
        public void run() {
            GitHubClient gitHubClient = new GitHubClient();
            gitHubClient.setCredentials(username, password);
            gitHubClient.setUserAgent(getString(R.string.app_name));

            /* 获取认证信息 */
            Authorization authorization = null;
            OAuthService oAuthService = new OAuthService(gitHubClient);
            try {
                List<Authorization> authorizationList = oAuthService.getAuthorizations();

                for (Authorization a : authorizationList) {
                    if (getString(R.string.app_name).equals(a.getNote())) {
                        authorization = a;
                        break;
                    }
                }

                /* 如果当前应用没有被认证，则新建认证 */
                if (authorization == null) {
                    authorization = new Authorization();
                    authorization.setNote(getString(R.string.app_name));
                    authorization.setUrl(getString(R.string.app_url));
                    List<String> scopes = new ArrayList<String>();
                    scopes.add(getString(R.string.login_permission_notifications));
                    scopes.add(getString(R.string.login_permission_repo));
                    scopes.add(getString(R.string.login_permission_user));
                    authorization.setScopes(scopes);
                    authorization = oAuthService.createAuthorization(authorization);
                }

                /* 将获取到的认真信息写入SharedPreferences */
                editor = sharedPreferences.edit();
                editor.putString(getString(R.string.login_sp_username), username);
                editor.putString(getString(R.string.login_sp_oauth), authorization.getToken());
                editor.putInt(getString(R.string.login_sp_highlight_num), 0);
                editor.putString(getString(R.string.login_sp_highlight_css), getString(R.string.webview_default_css));
                editor.commit();
                progressDialog.dismiss();

                /* 然后开启MainActivity */
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra(getString(R.string.login_intent), true);
                startActivity(intent);
                finish();
            } catch (IOException i) {
                progressDialog.dismiss();
                SuperToast.create(
                        LoginActivity.this,
                        getString(R.string.login_message_auth_failed),
                        SuperToast.Duration.SHORT,
                        Style.getStyle(Style.RED)
                ).show();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.login_menu_about:
                /* Do something */
                break;
            default:
                break;
        }
        return true;
    }
}
