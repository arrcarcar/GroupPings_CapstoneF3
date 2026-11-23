package Swing.Accounts;

import java.util.ArrayList;

public class AccountManager {
    private ArrayList<Account> accounts;
    public Account logIn(String username, String password){
        for(Account a : accounts){
            if(a.getUsername().equals(username) && a.getPassword().equals(password)){
                return a;
            }
        }
        return null;
    }

    public void add(Account acc){
        accounts.add(acc);
    }
}
