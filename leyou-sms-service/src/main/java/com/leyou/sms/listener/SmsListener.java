package com.leyou.sms.listener;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.leyou.sms.properties.SmsProperties;
import com.leyou.sms.util.SmsUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsListener {

    @Autowired
   SmsProperties smsProperties;

    @Autowired
    private SmsUtil smsUtil;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.SMS.QUEUE", durable = "true"),
            exchange = @Exchange(value = "LEYOU.SMS.EXCHANGE",
                    ignoreDeclarationExceptions = "true"),
            key = {"sms.verify.code"}))
    public void listenSms(Map<String,String>sms) throws ClientException {

        //如果sms为空，放弃处理
        if(sms == null || sms.size()==0){
            return;
        }
        //获取code，phone
        String code = sms.get("code");
        String phone = sms.get("phone");

        //判断code和phone是不是空的
        if(StringUtils.isBlank(code)||StringUtils.isBlank(phone)){
            return;
        }
        //发送短信
        SendSmsResponse response = this.smsUtil.send(phone, code, smsProperties.getSignName(), smsProperties.getVerifyCodeTemplate());

    }
}


