package com.microservices.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import com.microservices.dao.UserDAO;

public interface IUserRepository extends Remote {
  public UserDAO getUserById(String userId) throws RemoteException;
  public void createUser(UserDAO user) throws RemoteException;
}
