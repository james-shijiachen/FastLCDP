package cn.com.traninfo.fastlcdp.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.util.ArrayList;
import java.util.List;

@Getter
@Slf4j
public class XmlValidationErrorHandler implements ErrorHandler {

    private final Integer maxErrorCount;
    private final List<String> errors = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();

    public XmlValidationErrorHandler(Integer maxErrorCount) {
        this.maxErrorCount = maxErrorCount;
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        String message = formatMessage("警告", exception);
        warnings.add(message);
        log.warn(message);
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        String message = formatMessage("错误", exception);
        errors.add(message);
        log.error(message);
        if(null != maxErrorCount && errors.size() > maxErrorCount){
            throw exception;
        }
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        String message = formatMessage("严重错误", exception);
        errors.add(message);
        log.error(message);
        throw exception;
    }

    private String formatMessage(String level, SAXParseException exception) {
        return String.format("%s [行:%d, 列:%d]: %s",
                level,
                exception.getLineNumber(),
                exception.getColumnNumber(),
                exception.getMessage());
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

}
