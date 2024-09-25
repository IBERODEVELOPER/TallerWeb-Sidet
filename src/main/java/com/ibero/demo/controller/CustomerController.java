package com.ibero.demo.controller;

import com.ibero.demo.entity.Customer;
import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.Event;
import com.ibero.demo.service.CustomerService;
import com.ibero.demo.util.PageRender;

import jakarta.validation.Valid;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("customer")
@SessionAttributes("customer")
public class CustomerController {
	
	@Autowired
	private CustomerService CustomerService;
	
	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN", "ROLE_EMPLOYEE"})
	@GetMapping(value = "/listCustomers")
	public String ListPeople(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
		Pageable pageRequest = PageRequest.of(page, 8);
		Page<Customer> customers = CustomerService.findAllCustomer(pageRequest);
		// Calcula el total de registros
	    long totalRecords = customers.getTotalElements();
		PageRender<Customer> pageRender = new PageRender<Customer>("/customer/listCustomers", customers);
		model.addAttribute("titlepage", "Clientes registrados en el sistema");
		model.addAttribute("customers", customers);
		model.addAttribute("page", pageRender);
		model.addAttribute("totalRecords", totalRecords);
		return "/pages/allCustomer";
	}
	
	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN", "ROLE_EMPLOYEE"})
	@GetMapping(value = "/formCustomer")
	public String showForm(Map<String, Object> model) {
		Customer customer = new Customer();
		model.put("titlepage", "Registro de datos del Cliente");
		model.put("titleform", "Ficha de Alta Cliente");
		model.put("customer", customer);
		return "/pages/formCustomer";
	}
	
	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN", "ROLE_EMPLOYEE"})
	@GetMapping(value = "/formCustomer/{id}")
	public String editForm(@PathVariable(value = "id") int id, Map<String, Object> model, RedirectAttributes flash) {
		if (id <= 0) {
			flash.addFlashAttribute("error", "El ID del cliente no puede ser 0");
			return "redirect:/customer/listCustomers";
		}
		Customer customer = CustomerService.findCustomerById(id);
		if (customer == null) {
			flash.addFlashAttribute("error", "El ID del cliente no existe en la BBDD");
			return "redirect:/peoples/listPeople";
		}
		model.put("titlepage", "Registro de datos del Cliente");
		model.put("titleform", "Ficha de Alta Cliente");
		model.put("customer", customer);
		return "/pages/formCustomer";
	}
	
	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN", "ROLE_EMPLOYEE"})
	@PostMapping(value = "/formCustomer")
	public String processForm(@Valid Customer customer,BindingResult result, Model model,RedirectAttributes flash, SessionStatus status) {
		if (result.hasErrors()) {
			model.addAttribute("titlepage", "Formulario de Registro de Clientes");
			model.addAttribute("titleform", "Registro de Datos");
			return "/pages/formPeople";
		}
		
		if (customer.getId() == 0) {
			CustomerService.saveCustomer(customer);
			flash.addFlashAttribute("success", "Datos registrados correctamente");

		} else {
			// Actualizar solo los campos específicos si es necesario
			CustomerService.saveCustomer(customer);
			flash.addFlashAttribute("success", "Datos Actualizados correctamente");
		}
		status.setComplete();
		return "redirect:/customer/listCustomers";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping(value = "/deleteCustomerById/{id}")
	public String deleteById(@PathVariable(value = "id") Integer id,RedirectAttributes flash) {
		if(id > 0) {
			Customer customer  = CustomerService.findCustomerById(id);
			CustomerService.deleteCustomerById(customer.getId());
			flash.addFlashAttribute("success", "Cliente "+ customer.getIdentityCustomer()+" eliminado con éxito");			
		}
		return "redirect:/customer/listCustomers";
	}
}
