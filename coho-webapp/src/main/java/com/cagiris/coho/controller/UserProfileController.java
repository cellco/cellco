/*
 * Copyright (c) 2015, Cagiris Pvt. Ltd.
 * All rights reserved.
 */
package com.cagiris.coho.controller;

import java.io.Serializable;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cagiris.coho.model.UserProfileBean;
import com.cagiris.coho.service.api.IHierarchyService;
import com.cagiris.coho.service.exception.CohoException;

/**
 * @author Ashish Jindal
 *
 */
@Controller
@RequestMapping(UserProfileController.URL_MAPPING)
public class UserProfileController extends AbstractCRUDController<UserProfileBean> {

    public static final String URL_MAPPING = "user-profile";

    @Autowired
    private IHierarchyService hierarchyService;
    
	@Override
	protected String getURLMapping() {
		return URL_MAPPING;
	}

	@Override
	protected ModelMap getCreateFormModel() throws CohoException {
		throw new CohoException(Constants.ERROR_FORBIDDEN);
	}

	@Override
	protected ModelMap create(UserProfileBean bean, ModelMap modelMap)
			throws CohoException {
		throw new CohoException(Constants.ERROR_FORBIDDEN);
	}

	@Override
	protected void delete(Serializable entityId) throws CohoException {
		throw new CohoException(Constants.ERROR_FORBIDDEN);
	}

	@Override
	protected ModelMap get(Serializable entityId) throws CohoException {
		ModelMap modelMap = new ModelMap();

		UserProfileBean userProfile = new UserProfileBean(hierarchyService.getUserProfile((String) entityId));
		modelMap.addAttribute(userProfile);

		return modelMap;
	}

	@Override
	protected ModelMap getListFormModel() throws CohoException {
		throw new CohoException(Constants.ERROR_FORBIDDEN);
	}

	@Override
	protected ModelMap getListData(Map<String, String> params)
			throws CohoException {
		throw new CohoException(Constants.ERROR_FORBIDDEN);
	}

	@Override
	protected ModelMap update(Serializable entityId, UserProfileBean bean,
			ModelMap modelMap) throws CohoException {
		ModelMap responseModelMap = new ModelMap();

		hierarchyService.updateUserProfile(bean.getUserId(), 
											bean.getFirstName(), 
											bean.getLastName(), 
											bean.getDateOfBirth(), 
											bean.getGender(), 
											bean.getMobileNumber(), 
											bean.getEmailId(), 
											bean.getAddressLine1(), 
											bean.getAddressLine2(), 
											bean.getCity(), 
											bean.getPincode(), 
											bean.getState(), 
											bean.getCountry(), 
											bean.getJoinedOn(), 
											bean.getLeftOn(), 
											bean.getDesignation());

		responseModelMap.addAttribute(ATTR_SUCCESS_MSG, "User profile successfuly updated.");

		return responseModelMap;
	}
	
	@RequestMapping(value = {"", "/"})
    public final ModelAndView showProfile() throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(getURLMapping() + GET_URL_MAPPING);

		String entityId = ControllerUtils.getLoggedInUser().getUsername();
        modelAndView.addAllObjects(get(entityId));

        return modelAndView;
    }
}
