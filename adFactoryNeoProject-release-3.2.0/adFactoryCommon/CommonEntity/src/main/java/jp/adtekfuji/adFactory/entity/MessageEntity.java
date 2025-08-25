package jp.adtekfuji.adFactory.entity;

import jakarta.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "resultResponse")
public class MessageEntity  implements Serializable {

    @XmlElement()
    private String format;

    @XmlElement()
    private String addInfo;

    @XmlElementWrapper(name = "args")
    @XmlElement(name = "arg")
    private List<String> args;

    public MessageEntity() {
    }

    public MessageEntity(String format, List<String> args) {
        this.format = format;
        this.args = args;
    }

    public MessageEntity(String format, String... args) {
        this.format=format;
        this.args = Arrays.asList(args);
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public String getAddInfo() {
        return addInfo;
    }

    public void setAddInfo(String addInfo) {
        this.addInfo = addInfo;
    }
}
