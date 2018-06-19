package com.example.team4kb.smartbabymonitor;

/**
 * Created by User on 2018-05-24.
 */

public class User {
    private String email;
    private String token;
    private String raspberry;

    User (String _email, String _token, String _raspberry)
    {
        email = _email;
        token = _token;
        raspberry = _raspberry;
    }

    public String getEmail()
    {
        return email;
    }

    public String getToken()
    {
        return token;
    }

    public String getRaspberry()
    {
        return raspberry;
    }

    public void setEmail(String _email)
    {
        email = _email;
    }

    public void setToken(String _token)
    {
        token = _token;
    }

    public void setRaspberry(String _raspberry)
    {
        raspberry = _raspberry;
    }
}
