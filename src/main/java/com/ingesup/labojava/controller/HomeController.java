package com.ingesup.labojava.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.ingesup.labojava.bean.FriendRequest;
import com.ingesup.labojava.bean.User;
import com.ingesup.labojava.form.AnnonceFormBean;
import com.ingesup.labojava.form.Filter;
import com.ingesup.labojava.form.FilterCategory;
import com.ingesup.labojava.service.UserService;
import com.ingesup.labojava.service.UserServiceImpl;

/**
 * Handles requests for the application home page.
 */
@Controller
@SessionAttributes("user")
public class HomeController {
	
	
	/* INJECTION DE DEPENDANCES */
	
	private UserService userService = new UserServiceImpl();

	@Autowired(required = true)
	@Qualifier(value = "userService")
	public void setUserService(UserService us) {
		this.userService = us;
	}
	
	
	/* Page d'accueil */

	@RequestMapping(value={"/", "/home**"}, method = RequestMethod.GET)
	public String homePage(final Model model) {
		
		/* CHARGEMENT DES BEANS */
		
		model.addAttribute("adBean", new AnnonceFormBean());
		model.addAttribute("usersCount", userService.countUsers());
		model.addAttribute("latestUsers", userService.getLatestUsers(10));
		model.addAttribute("latestAnnonces", userService.getLatestAnnonces(5));
		
		/* afficher uniquement les filtres concern�es */

		List<Filter> allFilters = Utilities.createFilters(userService.getAllAds());
		model.addAttribute("locationFilters", Utilities.getFiltersByCategory(allFilters, FilterCategory.LOCATION));
		model.addAttribute("subjectFilters", Utilities.getFiltersByCategory(allFilters, FilterCategory.SUBJECT));
		model.addAttribute("levelFilters", Utilities.getFiltersByCategory(allFilters, FilterCategory.LEVEL));
		
		
		/* Initialiser les beans de sessions */
		
		
		
		
		return "home";
	}
	
	/* Page du GUIDE */
	
	@RequestMapping(value="/guide", method = RequestMethod.GET)
	public String displayGuidePage() {
		return "guide";
	}
	
	
	/**
	 * PAGE DE TEST * */
	
	@RequestMapping(value="/test-page")
	public String displayTestPage(final Model model) {
		
		model.addAttribute("userList", userService.getAllUsers());
		return "test-page";
	}
	
	
	
	/**
	 * ENVOYER UNE DEMANDE * */
	
	@RequestMapping(value="/inviter/{senderID}_{receiverID}", method = RequestMethod.GET)
	public String sendFriendRequest(@PathVariable("senderID") Long senderID, 
			@PathVariable("receiverID") Long receiverID, final Model model) {
		
		System.out.println("///////// ENVOIE DE L'INVITATION EN COURS...");
		
		/* Recherche des utilisateurs */
		
		User senderUser = userService.getUser(senderID);
		User receiverUser = userService.getUser(receiverID);
		
		if (senderUser == null || receiverUser == null) {
			model.addAttribute("status", "La ressource 'Utilisateur' est introuvable");
			return "redirect:/statusPage";
		}
		
		System.out.println("////// USERS FOUNDS!!!");
		System.out.println("//// FriendRequestsIN (size)" +receiverUser.getFriendRequests().size());
		System.out.println("//// FriendRequestsOUT (size)" +senderUser.getFriendRequests().size());
		
		/* Friend request */
		
		FriendRequest receivedRequest = new FriendRequest(senderUser, receiverUser, true);
		FriendRequest sentRequest = new FriendRequest(senderUser, receiverUser, false);
		
		receiverUser.addFriendRequest(receivedRequest);
		senderUser.addFriendRequest(sentRequest);
		
		System.out.println("//// FriendRequestsIN (size)" +receiverUser.getFriendRequests().size());
		System.out.println("//// FriendRequestsOUT (size)" +senderUser.getFriendRequests().size());
		
		userService.updateUser(receiverUser);
		userService.updateUser(senderUser);
		
		System.out.println("/////// UPDATED MyFRIENDREQUESTS_IN : " +receiverUser.getFriendRequests().size());
		
		return "redirect:/test-page";
	}
	
	/**
	 * GESTION D'UNE DEMANDE ACCEPT - REFUSE
	 * */
	
	@RequestMapping(value="/friend-request:{response}/{reqID}", method = RequestMethod.GET)
	public String processFriendRequest(@PathVariable("response") String response, @PathVariable("reqID") Long reqID, 
			final Model model) {
		
		System.out.println("//////// FRIEND REQUEST PROCESSING...");
		
		
		/* Get the concerning Request */
		
		FriendRequest fRequest = userService.getFriendRequest(reqID);
		
		if (fRequest == null) {
			
			model.addAttribute("status", "La ressource demand�e est introuvable!");
			return "statusPage";
		}
		
		/* On ajoute l'ami dans sa liste d'amis (deux sens)
		 * On supprime la demande des listes (deux sens)
		 * 
		*/ 
		
		User sender = userService.getUser(fRequest.getSenderID());
		User receiver =  userService.getUser(fRequest.getReceiverID());
		
		if (sender == null || receiver == null) {
			model.addAttribute("status", "La ressource 'Utilisateur' est introuvable");
			return "redirect:/statusPage";
		}
		
		System.out.println("//////// Utilisateurs trouv�s!");
		
		System.out.println("///////// FRIENDLIST (sender)" +sender.getMyFriends().size());
		System.out.println("///////// FRIENDLIST (receiver)" +receiver.getMyFriends().size());
		
		receiver.removeSentFriendRequest(fRequest);
		sender.removeFriendRequest(fRequest);
		
		
		if ("accept".equals(response)) {
			sender.addFriend(receiver);
			receiver.addFriend(sender);
		}
		
		System.out.println("///////// FRIENDLIST_ADD (sender)" +sender.getMyFriends().size());
		System.out.println("///////// FRIENDLIST_ADD (receiver)" +receiver.getMyFriends().size());
		
		// Mise � jour des utilisateurs : ATTENTION --> Un seul �l�ment doit persister. En parcourant sa liste,
		// Il persistera automatiquement les autres �l�ments
		//userService.updateUser(sender);
		userService.updateUser(receiver);
		
		/** SUPPRIMER LES TABLES DE DEMANDES *
		 * 
		 * 
		 * */
		
		userService.removeFriendRequest(fRequest);
		
		sender = userService.getUser(fRequest.getReceiverID());
		
		System.out.println("///////// UP! FRIENDLIST (sender)" +sender.getMyFriends().size());
		System.out.println("///////// UP! FRIENDLIST_OF (sender)" +sender.getFriendOf().size());
		
		
		return "redirect:/test-page";
	}
	
	/** SUPPRIMER UN AMI */
	
	@RequestMapping(value="/removeContact/{userID}:{cID}", method = RequestMethod.GET)
	public String removeContact(@PathVariable("userID") Long userID, @PathVariable("cID") Long cID, final Model model) {
		
		String URL = "test-page";
		
		System.out.println("Suppression d'un contact...");
		
		User contact = userService.getUser(cID);
		User currentUser = userService.getUser(userID);
		
		/** VERIFIER QUE LA SESSION EST ACTIVE  */
		
		if (currentUser == null || contact == null) {
			model.addAttribute("status", "Utilisateur introuvable!");
			return "redirect:/statusPage";
		}
		
		/* Utilisateurs trouv�s */
		
		currentUser.removeFriend(contact.getId());
		contact.removeFriend(currentUser.getId());
		
		// Mettre � jour les deux car il n'y a plus de mise � jour r�currente
		userService.updateUser(currentUser);
		userService.updateUser(contact);
		
		System.out.println("Contact supprim� avec succ�s!"); 
		
		return "redirect:/"+URL;
	}
	
}
