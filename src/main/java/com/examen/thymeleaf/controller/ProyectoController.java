package com.examen.thymeleaf.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.examen.web.bd.MySQLDataSource;
import com.examen.web.model.User;
import com.examen.web.repository.IPersonRepository;
import com.examen.web.repository.IRoleRepository;
import com.examen.web.repository.IUserRepository;
import com.examen.web.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

@Controller
public class ProyectoController {
	
	@Autowired
	private IRoleRepository repos;
	
	@Autowired
	private IPersonRepository reposPerson;
	
	@Autowired
	private IUserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	
	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
	    model.addAttribute("userRegister", new User()); // Se agrega un nuevo objeto User al modelo
	    return "registrar"; // Devuelve la vista 'register.html'
	}

	
	@PostMapping("/register")
	public String registerUser(@ModelAttribute("userRegister") User user, Model model) {
	    try {
	        // Verifica si el correo ya está registrado
	        if (userService.findByEmail(user.getEmail()) != null) {
	            model.addAttribute("error", "El correo ya está registrado");
	            return "register";
	        }
	        
	        // Guardar el nuevo usuario
	        userService.saveUser(user);
	        return "login"; // Redirige al login tras un registro exitoso
	    } catch (Exception e) {
	        model.addAttribute("error", "Ocurrió un error durante el registro");
	        return "register";
	    }
	}

	
	
	@GetMapping("/listar")
	public String listRole(Model model) {
		try {
			model.addAttribute("ltsRole", repos.findAll());
			model.addAttribute("ltsPerson", reposPerson.findAll());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "listado";
	}
	
	@GetMapping("/login")
	public String loginView(Model model) {
		System.out.println("Mostrando login");
		model.addAttribute("userLogin", new User());
		return "login";
	}
	
	@PostMapping("/login")
	public String login(@ModelAttribute User user, Model model) {
		System.out.println("Validando login");		
		String redirect = "login";
		User u = userService.validateUserByEmailAndPassword(user.getEmail(), user.getPassword());
		if (u != null) {
			u.setLastlogin(new Date());
			System.out.println("Actualizando usuario");
			User updUser = userService.updateUserLogin(u);
			model.addAttribute("userLogin", updUser);
			redirect = "inicio";
		} else {
			model.addAttribute("errors", "Usuario o clave incorrectos");
			model.addAttribute("userLogin", new User());
		}
		
		return redirect;
	}
	
	
	
	
	@RequestMapping(value = "/PersonReport", method = RequestMethod.GET)
	public void personaReporte(HttpServletResponse response) throws JRException, IOException {
		System.out.println("Hola mundo");
		InputStream jasperStream = this.getClass().getResourceAsStream("/reporte/Wood.jasper");
		System.out.println(jasperStream);
		Map<String, Object> params = new HashMap<String, Object>();
		JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperStream);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, MySQLDataSource.getMySQLConnection());
		response.setContentType("application/x-pdf");
		response.setHeader("Content-disposition", "inline; filename=person-report.pdf");
		final OutputStream outputStream = response.getOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
	}
	
}
