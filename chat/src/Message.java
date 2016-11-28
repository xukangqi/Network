import java.util.Date;

/**
 * Created by Administrator on 2016/4/3.
 */
public class Message {
    public Date SendDate;
    public String from;
    public String To;
    public String MessageType;
    public String Content;

    public Message(){
        SendDate=null;
        from="";
        To="";
        MessageType="";
        Content="";
    }
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessageType() {
        return MessageType;
    }

    public void setMessageType(String messageType) {
        MessageType = messageType;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String to) {
        To = to;
    }

    public Date getSendDate() {

        return SendDate;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public void setSendDate(Date sendDate) {
        SendDate = sendDate;
    }
}
