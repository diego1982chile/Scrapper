package cl.ctl.scrapper.model;

/**
 * Created by root on 09-12-22.
 */
public enum ParameterEnum {

    USER_NAME("username"),
    PASSWORD("password"),
    BASE_URL_TOKEN("base_url_token"),
    BASE_URL_CONFIG("base_url_config"),
    RETAILER("retailer"),
    TOKEN("token"),
    CAPTCHA_API_KEY("captcha.api_key"),
    ERROR_TO("error.to"),
    FILE_DOWNLOAD_PATH("file.download_path"),
    MAIL_FROM_PASSWORD("mail.from.password"),
    MAIL_FROM_USER("mail.from.user"),
    MAIL_TO("mail.to"),
    UPLOAD_HOST("upload.host"),
    UPLOAD_PASSWORD("upload.password"),
    UPLOAD_PATH("upload.path"),
    UPLOAD_SERVER("upload.server"),
    UPLOAD_TARGET("upload.target");

    private String parameter;

    public String getParameter() {
        return this.parameter;
    }

    ParameterEnum(String parameter) {
        this.parameter = parameter;
    }
}
