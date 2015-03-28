/*
 * Copyright (c) 2015, Cagiris Pvt. Ltd.
 * All rights reserved.
 */
package com.cagiris.coho.service.entity;

import java.util.Map;

import javax.persistence.Entity;

import com.cagiris.coho.service.api.IUserRoleLeaveQuota;
import com.cagiris.coho.service.api.LeaveType;
import com.cagiris.coho.service.api.UserRole;

/**
 *
 * @author: ssnk
 */

@Entity(name = "userRoleLeaveQuota")
public class UserRoleLeaveQuotaEntity extends BaseEntity implements IUserRoleLeaveQuota {

	private UserRole userRole;
	private Map<LeaveType, Integer> leaveTypeVsLeaveCount;

	@Override
	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	@Override
	public Map<LeaveType, Integer> getLeaveTypeVsLeaveCount() {
		return leaveTypeVsLeaveCount;
	}

	public void setLeaveTypeVsLeaveCount(
			Map<LeaveType, Integer> leaveTypeVsLeaveCount) {
		this.leaveTypeVsLeaveCount = leaveTypeVsLeaveCount;
	}



}