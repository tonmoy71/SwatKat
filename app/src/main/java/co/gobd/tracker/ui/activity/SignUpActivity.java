package co.gobd.tracker.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import co.gobd.tracker.R;
import co.gobd.tracker.application.GoAssetApplication;
import co.gobd.tracker.presenter.SignUpPresenter;
import co.gobd.tracker.ui.view.SignUpView;

public class SignUpActivity extends AppCompatActivity implements SignUpView{

    @Inject
    SignUpPresenter signUpPresenter;

    @Inject
    Context context;

    @BindView(R.id.et_username)
    EditText etUsername;

    @BindView(R.id.et_password)
    EditText etPassword;

    @BindView(R.id.et_confirm_password)
    EditText etConfirmPassword;

    @BindView(R.id.et_email)
    EditText etEmail;

    @BindView(R.id.et_phone)
    EditText etPhone;

    @BindView(R.id.btn_sign_up)
    Button btnSignUp;

    private Unbinder unbinder;

    ProgressDialog progressDialog;

    @Override
    protected void onDestroy(){
        super.onDestroy();
        signUpPresenter.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        unbinder = ButterKnife.bind(this);
        ((GoAssetApplication) getApplication()).getComponent().inject(this);
        signUpPresenter.initialise(this);
    }

    @OnClick(R.id.btn_sign_up)
    public void onClick() {
        if (signUpPresenter.isValidCredentials()) {
            signUpPresenter.register();
        }

    }

    @Override
    public String getUserName() {
        return etUsername.getText().toString().trim();
    }

    @Override
    public void showUserNameEmptyError() {
        etUsername.setError("Username can't be empty");
    }

    @Override
    public String getPassword() {
        return etPassword.getText().toString();
    }

    @Override
    public void showPasswordEmptyError() {
        etPassword.setError("Password can't be empty");
    }

    @Override
    public void showPasswordLengthError() {
        etPassword.setError("Password must be at least 6 characters long");
    }

    @Override
    public String getConfirmPassword() {
        return etConfirmPassword.getText().toString();
    }

    @Override
    public void showPasswordMatchError() {
        etPassword.setError("Password and confirm password didn't match");
    }

    @Override
    public String getEmail() {
        return etEmail.getText().toString();
    }

    @Override
    public void showInvalidEmailPatterError() {
        etEmail.setError("Invalid email");
    }

    @Override
    public boolean isEmailPatternValid() {
        return Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches();
    }

    @Override
    public String getPhoneNumber() {
        return etPhone.getText().toString();
    }


    @Override
    public void stopProgress() {
        progressDialog.dismiss();
    }

    @Override
    public void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void startProgress() {
        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Registering... ");
        progressDialog.show();
    }

    @Override
    public void showRegistrationError(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showConnectionError() {
        Toast.makeText(context, "Can't connect to server", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean isPhoneNumberValid() {
        return Patterns.PHONE.matcher(getPhoneNumber()).matches();
    }

    @Override
    public void showInvalidPhoneNumberError() {
        etPhone.setError("Invalid phone number");
    }

    @Override
    public void showUserNameHasSpaceError() {
        etUsername.setError("Username can't have a blank space");
    }
}
