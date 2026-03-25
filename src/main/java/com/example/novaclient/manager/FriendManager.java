package com.example.novaclient.manager;

import java.util.HashSet;
import java.util.Set;

public class FriendManager {
    private final Set<String> friends = new HashSet<>();
    
    public void addFriend(String name) {
        friends.add(name.toLowerCase());
    }
    
    public void removeFriend(String name) {
        friends.remove(name.toLowerCase());
    }
    
    public boolean isFriend(String name) {
        return friends.contains(name.toLowerCase());
    }
    
    public Set<String> getFriends() {
        return new HashSet<>(friends);
    }
}