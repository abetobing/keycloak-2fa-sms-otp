<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
    <#if section = "title">
        ${msg("otp")}
    <#elseif section = "header">
        ${msg("otp")}
    <#elseif section = "form">
        <form id="kc-form-send-otp"
              class="box"
              action="${url.loginAction}" method="post">
                <div class="notification is-info ${properties.kcFormGroupClass!}">
                    ${msg("otpSentTo",(maskedPhoneNumber!''))}
                </div>

            <div class="field ${properties.kcFormGroupClass!}">
                <label for="otp-input" class="label ${properties.kcLabelClass!}">${msg("otp")}</label>

                <div class="control">
                    <input tabindex="2" id="otp-code-input" class="input ${properties.kcInputClass!}" name="otpInput" type="text" autocomplete="off"
                           aria-invalid="<#if messagesPerField.existsError('otpInput')>true</#if>"
                    />
                </div>
                <p class="help is-success">${msg("helpInputOtp")}</p>

            </div>

            <div id="kc-form-buttons" class="field ${properties.kcFormGroupClass!}">
                <input type="hidden" id="id-hidden-input" name="credentialId" <#if auth.selectedCredential?has_content>value="${auth.selectedCredential}"</#if>/>
                <div class="control">
                    <input tabindex="4" class="button is-primary ${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                           name="login" id="kc-login" type="submit"
                           value="${msg("doValidateOtp")}"/>
                    <input tabindex="5" class="button ${properties.kcButtonClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                           name="cancel" id="kc-login" type="submit"
                           value="${msg("doCancel")}"/>
                </div>
                <p class="help is-success">
                    ${msg("didntReceive")} <a href="#">${msg("doResend")}</a>
                </p>

            </div>
        </form>


    </#if>
</@layout.registrationLayout>
