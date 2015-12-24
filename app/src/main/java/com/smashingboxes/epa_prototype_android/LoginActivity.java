package com.smashingboxes.epa_prototype_android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.smashingboxes.epa_prototype_android.fitbit.UriParser;
import com.smashingboxes.epa_prototype_android.fitbit.auth.FitbitAuthController;
import com.smashingboxes.epa_prototype_android.fitbit.auth.FitbitLoginCache;
import com.smashingboxes.epa_prototype_android.fitbit.auth.FitbitOAuth2;
import com.smashingboxes.epa_prototype_android.fitbit.models.FitbitAuthModel;

/**
 * Created by Austin Lanier on 12/8/15.
 * <p/>
 *
 * We need a seperate activity for this, rather than doing the OAuth2 flow natively.
 *
 * From the https://dev.fitbit.com/docs/oauth2/ page:
 * <p/>
 * For security consideration, the OAuth 2.0 authorization page must be presented in a dedicated browser view. Fitbit users can only confirm they are authenticating with the genuine Fitbit.com site if they have they have the tools provided by the browser, such as the URL bar and Transport Layer Security (TLS) certificate information.
 * <p/>
 * For native applications, this means the authorization page must open in the default browser. Native applications can use custom URL schemes as callback URIs to redirect the user back from the browser to the application requesting permission.
 * <p/>
 * iOS applications may use the SFSafariViewController class instead of app switching to Safari. Use of the WKWebView or UIWebView class is prohibited.
 * <p/>
 * Android applications may use Chrome Custom Tabs instead of app switching to the default browser. Use of WebView is prohibited.
 * <p/>
 *
 * So, we have to use Chrome Custom Tabs if we want to make things pretty (if we have time), and otherwise launch an implicit intent.
 */
public class LoginActivity extends AppCompatActivity implements FitbitAuthController {

    private static final String TAG = LoginActivity.class.getName();

    public static final String ACTION_LOGOUT = "com.smashingboxes.epa_prototype_android.ACTION_LOGOUT";

    private FitbitLoginCache mFitbitLoginCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        mFitbitLoginCache = FitbitLoginCache.getInstance(this);

        Uri redirect_uri = intent.getData();
        if (redirect_uri != null) {
            handleRedirectUri(redirect_uri);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FitbitAuthModel authModel = mFitbitLoginCache.getLoginModel();
        if(authModel != null){
            if(authModel.isExpired()){
                sendFitbitRefreshRequest();
            } else {
                onFitbitUserAuthenticated();
            }
        } else {
            startAuthenticationFlow();
        }
    }

    @Override
    public void startAuthenticationFlow() {
        FitbitOAuth2 auth2 = new FitbitOAuth2(FitbitOAuth2.AuthFlowType.IMPLICIT);
        auth2.launchAuthenticationIntent(this, auth2.getUrlBuilder().buildDefault(this));
    }

    @Override
    public void handleRedirectUri(Uri uri) {
        try {
            FitbitOAuth2 auth2 = new FitbitOAuth2(FitbitOAuth2.AuthFlowType.IMPLICIT);
            FitbitAuthModel authModel = (FitbitAuthModel) auth2.getParser(uri).parseUri();
            mFitbitLoginCache.saveLoginModel(authModel);
            onFitbitUserAuthenticated();
        } catch (UriParser.ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Login Failure", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFitbitUserAuthenticated() {
        Intent toLoggedInActivity = new Intent(this, MainActivity.class);
        toLoggedInActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toLoggedInActivity);
    }

    //UNUSED FOR NOW UNTIL WE HAVE A WAY TO USE OUR CLIENT SECRET, FOR NOW
    //WE USE THE IMPLICIT FITBIT FLOW RATHER THAN THE AUTHORIZATION FLOW
    @Override
    public void sendFitbitRefreshRequest() {
        mFitbitLoginCache.clearLogin();
        startAuthenticationFlow(); //delegate to auth flow for now
    }
}

