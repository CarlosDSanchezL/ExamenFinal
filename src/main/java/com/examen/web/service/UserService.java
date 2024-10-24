package com.examen.web.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.examen.web.model.User;
import com.examen.web.repository.IUserRepository;


@Service
public class UserService {
	
	@Autowired
	private IUserRepository userRepository;
	
	public User validateUserByEmailAndPassword(String email, String password) {
		User u = userRepository.findByEmailAndPassword(email, password);
		return u;
	}
	
	public User updateUserLogin(User user) {
//		User u = userRepository.getReferenceById(user.getIduser());
//		u.setLastlogin(new Date());
//		return userRepository.save(u);
		return userRepository.save(user);
	}
	
	
    public void saveUser(User user) {
        // Aquí podrías añadir lógica para encriptar la contraseña antes de guardar
        user.setPassword(encryptPassword(user.getPassword()));
        userRepository.save(user);
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    private String encryptPassword(String password) {
        // Implementar la encriptación de la contraseña
        return password; // Esto debe ser reemplazado con una encriptación real, como BCrypt
    }
}
