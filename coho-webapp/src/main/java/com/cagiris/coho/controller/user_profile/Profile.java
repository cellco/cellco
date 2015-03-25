/*
 * Copyright (c) 2015, Cagiris Pvt. Ltd.
 * All rights reserved.
 */
package com.cagiris.coho.controller.user_profile;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Ashish Jindal
 *
 */
@Controller
public class Profile {

	@RequestMapping(value = {"/profile"}, method = RequestMethod.GET)
	public String showProfilePage () {
		return "profile";
	}
}