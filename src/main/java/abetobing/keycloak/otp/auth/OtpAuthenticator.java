package abetobing.keycloak.otp.auth;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.services.messages.Messages;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.List;

@JBossLog
public class OtpAuthenticator implements Authenticator {

    private static final String MOBILE_NUMBER_FIELD = "mobile_number";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        // TODO: get user's mobile then send OTP
        challenge(context, null);
    }

    private void challenge(AuthenticationFlowContext context, FormMessage errorMessage) {
        LoginFormsProvider form = context.form().setExecution(context.getExecution().getId());
        if (errorMessage != null) {
            form.setErrors(List.of(errorMessage));
        }

        UserModel user = context.getUser();
        String phoneNumber = user.getFirstAttribute(MOBILE_NUMBER_FIELD);
//        if (phoneNumber == null) {
//            log.error("User has no phone number");
//            context.getEvent().error("user_has_no_phone_number");
//            context.success();
//            return;
//        }
        form.setAttribute("maskedPhoneNumber", "081xxxxxxx99");

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

        // TODO: validate OTP input
        String otpInput = context.getHttpRequest().getDecodedFormParameters().getFirst("otpInput");
        if (!validateInput(otpInput)) {
            context.attempted();
            challenge(context, new FormMessage(Messages.INVALID_CODE));
            return;
        }
        context.success();
    }

    private boolean validateInput(String otpInput) {
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
