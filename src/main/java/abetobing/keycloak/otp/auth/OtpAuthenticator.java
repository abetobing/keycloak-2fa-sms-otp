package abetobing.keycloak.otp.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.services.messages.Messages;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.List;

@JBossLog
@Getter
@Setter
public class OtpAuthenticator implements Authenticator {

    private static final String MOBILE_NUMBER_FIELD = "mobile_number";
    private static final String OTP_CODE_INPUT_FIELD = "otp_code";

    private static final String QUERY_STRING_RESEND = "resendOtp";

    private static final String RESEND_KEY = "key";
    private String mobileNumber;

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        UserModel user = context.getUser();
        // Check if resend code exist and valid
        String key = context.getAuthenticationSession().getAuthNote(RESEND_KEY);
        if (key != null) {
            String requestKey = context.getHttpRequest().getUri().getQueryParameters().getFirst(QUERY_STRING_RESEND);
            if (requestKey != null) {
                if (!requestKey.equals(key)) {
                    context.failure(AuthenticationFlowError.EXPIRED_CODE);
                    return;
                }
            }
        }

        mobileNumber = user.getFirstAttribute(MOBILE_NUMBER_FIELD);
//        if (mobileNumber == null) {
//            log.error("User has no phone number");
//            context.getEvent().error("user_has_no_phone_number");
//            context.success();
//            return;
//        }
        // TODO: get user's mobile then send OTP
        log.infof("Sending OTP to: %s", mobileNumber);
        challenge(context, null);
    }

    private void challenge(AuthenticationFlowContext context, FormMessage errorMessage) {
        LoginFormsProvider form = context.form().setExecution(context.getExecution().getId());
        if (errorMessage != null) {
            form.setErrors(List.of(errorMessage));
        }

        String key = KeycloakModelUtils.generateId();
        context.getAuthenticationSession().setAuthNote(RESEND_KEY, key);
        String resendLink = KeycloakUriBuilder.fromUri(context.getRefreshExecutionUrl()).queryParam(QUERY_STRING_RESEND, key).build().toString();


        form.setAttribute("maskedPhoneNumber", "081xxxxxxx99");
        form.setAttribute("resendLink", resendLink);

        Response response = form.createForm("otp-form.ftl");
        context.challenge(response);
    }

    @Override
    public void action(AuthenticationFlowContext context) {

        MultivaluedMap<String, String> form = context.getHttpRequest().getDecodedFormParameters();

        if (form.containsKey("cancel")) {
            context.cancelLogin();
            return;
        }

        String otpInput = context.getHttpRequest().getDecodedFormParameters().getFirst(OTP_CODE_INPUT_FIELD);
        if (!validateInput(otpInput)) {
            context.attempted();
            challenge(context, new FormMessage(Messages.INVALID_CODE));
            return;
        }
        context.success();
    }

    private boolean validateInput(String otpInput) {
        // this is just an example, do your own OTP code validation
        return otpInput.trim().equals("777999");
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        // NOOP
    }

    @Override
    public void close() {
        // NOOP
    }
}
