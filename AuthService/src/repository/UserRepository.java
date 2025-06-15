package repository;

import com.microservices.dao.UserDAO;
import com.microservices.interfaces.IUserRepository;
import java.rmi.RemoteException;
import java.sql.Connection;

public class UserRepository implements IUserRepository { 
  public UserDAO getUserById(String userId) throws RemoteException {
    return new UserDAO();
  }
  
  public void createUser(UserDAO user) throws RemoteException {
    
  }
}
