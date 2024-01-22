package com.driver;

import java.util.*;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashMap <String,User> userMap;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMap = new HashMap<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser (String name,String mobile) throws Exception{
        if(userMap.containsKey(mobile)){
            throw new Exception("User already exists");
        }
        userMap.put(mobile,new User(name,mobile));
        return "SUCCESS";
    }

    public Group createGroup (List<User> users) {
        int noOfParticipants = users.size();
        User admin = users.get(0);
        Group newGroup=new Group();
        if(noOfParticipants==2){
            newGroup.setName(users.get(1).getName());
        }
        else if (noOfParticipants>2){
            this.customGroupCount++;
            newGroup.setName("Group "+ customGroupCount);
        }
        groupUserMap.put(newGroup,users);
        adminMap.put(newGroup,admin);
        return newGroup;
    }

    public int createMessage(String content){
        messageId++;
        Message newmessage = new Message(messageId,content);
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "You are not allowed to send message" if the sender is not a member of the group
        //If the message is sent successfully, return the final number of messages in that group.
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }

        if(!groupUserMap.get(group).contains(sender)) {
            throw new Exception("You are not allowed to send message");
        }

        List <Message> msgList;
        if(!groupMessageMap.containsKey(group)){
            msgList=new ArrayList<>();
        }
        else msgList=groupMessageMap.get(message);

        msgList.add(message);
        groupMessageMap.put(group,msgList);
        senderMap.put(message,sender);
        return msgList.size();
    }


    public String changeAdmin(User approver, User user, Group group) throws Exception{
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "Approver does not have rights" if the approver is not the current admin of the group
        //Throw "User is not a participant" if the user is not a part of the group
        //Change the admin of the group to "user" and return "SUCCESS". Note that at one time there is only one admin and the admin rights are transferred from approver to user.
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }

        if(!adminMap.get(group).getMobile().equals(approver.getMobile()) ){
            throw new Exception("Approver does not have rights");
        }

        if(!groupUserMap.get(group).contains(user)) {
            throw new Exception("User is not a participant");
        }

        adminMap.put(group,user);
        return "SUCCESS";

    }


    public int removeUser(User user) throws Exception {
        //This is a bonus problem and does not contains any marks
        //A user belongs to exactly one group
        //If user is not found in any group, throw "User not found" exception
        //If user is found in a group and it is the admin, throw "Cannot remove admin" exception
        //If user is not the admin, remove the user from the group, remove all its messages from all the databases, and update relevant attributes accordingly.
        //If user is removed successfully, return (the updated number of users in the group + the updated number of messages in group + the updated number of overall messages)
        boolean flag = false;
        Group g = null;
        for (Group grp : groupUserMap.keySet()) {
            if (groupUserMap.get(grp).contains(user)) {
                g = grp;
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw new Exception("User not found");
        }

        for (User u : adminMap.values()) {
            if (u.getMobile() .equals(user.getMobile()) ) {
                throw new Exception("Cannot remove admin");
            }
        }
        if(groupMessageMap.containsKey(g)) {
            for (Message m : groupMessageMap.get(g)) {     // removing user's messages from groupMessage-MAP[Group--List<Message>]
                if (senderMap.get(m).getMobile().equals(user.getMobile())) {
                    groupMessageMap.get(g).remove(m);
                }
            }
        }

        for(User u:groupUserMap.get(g)) {  // removing user from groupUserMap[Group--List<User>]
            if(u.getMobile().equals(user.getMobile())) {
                groupUserMap.get(g).remove(u);
            }
        }

        for(Message m : senderMap.keySet()) { // removing [Message-User] entry from senderMap
            if(senderMap.get(m).getMobile().equals(user.getMobile())) {
                senderMap.remove(m);
            }
        }

        for(String phno: userMap.keySet()) {  //removing [phoneNo-User] entry from UserMap
            if(phno.equals(user.getMobile())) {
                userMap.remove(phno);
            }
        }

        return groupUserMap.get(g).size()+groupMessageMap.get(g).size()+senderMap.size();

    }


    public String findMessage(Date start, Date end, int K) throws Exception{
        //This is a bonus problem and does not contains any marks
        // Find the Kth latest message between start and end (excluding start and end)
        // If the number of messages between given time is less than K, throw "K is greater than the number of messages" exception

        return "";
    }


}

