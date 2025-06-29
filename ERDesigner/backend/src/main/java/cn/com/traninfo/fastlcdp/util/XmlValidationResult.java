package cn.com.traninfo.fastlcdp.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class XmlValidationResult {

    private Boolean valid;
    private String filePath;
    private List<String> errors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    public void addError(String error) {
        this.errors.add(error);
    }


    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    public Boolean isValid() {
        return valid;
    }

    public String getErrorMessage() {
        return errors.isEmpty() ? null : String.join("\n", errors);
    }

    public static XmlValidationResult success(){
        XmlValidationResult  result = new XmlValidationResult();
        result.valid = true;
        return result;
    }

    public static XmlValidationResult failure(String errorMessage){
        XmlValidationResult result = new XmlValidationResult();
        result.valid = false;
        result.errors.add(errorMessage);
        return result;
    }

}
