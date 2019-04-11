package com.it.bookstore;

import java.util.Date;

public class chatClass {

        private String messageText;
        private String messageUser,messageRecep;
        private String messageSender;
        private long messageTime;

        public chatClass(String messageText, String messageUser) {
            this.messageText = messageText;
            this.messageUser = messageUser;
            messageTime = new Date().getTime();
        }

        public chatClass(String messageText, String messageUser,String messageRecep,String messageSender) {
            this.messageText = messageText;
            this.messageUser = messageUser;
            this.messageSender = messageSender;
            this.messageRecep = messageRecep;
            messageTime = new Date().getTime();
        }

        public chatClass(){

        }

        public String getMessageText() {
            return messageText;
        }

        public void setMessageText(String messageText) {
            this.messageText = messageText;
        }

        public String getMessageUser() {
            return messageUser;
        }

        public String getMessageSender() { return messageSender; }

        public String getMessageRecep() { return messageRecep; }

        public void setMessageUser(String messageUser) {
            this.messageUser = messageUser;
        }

        public long getMessageTime() {
            return messageTime;
        }

        public void setMessageTime(long messageTime) {
            this.messageTime = messageTime;
        }
}
