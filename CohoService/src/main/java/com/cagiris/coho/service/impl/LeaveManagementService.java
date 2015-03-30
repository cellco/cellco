/*
 * Copyright (c) 2015, Cagiris Pvt. Ltd.
 * All rights reserved.
 */
package com.cagiris.coho.service.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cagiris.coho.service.api.ILeaveManagementService;
import com.cagiris.coho.service.api.IUserLeaveQuota;
import com.cagiris.coho.service.api.IUserLeaveRequest;
import com.cagiris.coho.service.api.IUserRoleLeaveQuota;
import com.cagiris.coho.service.api.IUserShiftInfo;
import com.cagiris.coho.service.api.LeaveRequestStatus;
import com.cagiris.coho.service.api.LeaveType;
import com.cagiris.coho.service.api.UserRole;
import com.cagiris.coho.service.db.api.DatabaseManagerException;
import com.cagiris.coho.service.db.api.EntityNotFoundException;
import com.cagiris.coho.service.db.api.IDatabaseManager;
import com.cagiris.coho.service.entity.QUserLeaveRequestEntity;
import com.cagiris.coho.service.entity.UserLeaveQuotaEntity;
import com.cagiris.coho.service.entity.UserLeaveRequestEntity;
import com.cagiris.coho.service.entity.UserRoleLeaveQuotaEntity;
import com.cagiris.coho.service.exception.LeaveManagementServiceException;
import com.cagiris.coho.service.utils.UniqueIDGenerator;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.mysema.query.types.CollectionExpression;

/**
 *
 * @author: phugga
 */

public class LeaveManagementService implements ILeaveManagementService {

	private static final Logger logger = LoggerFactory
			.getLogger(HierarchyService.class);

	private IDatabaseManager databaseManager;

	private UniqueIDGenerator leaveRequestIdGenerator = new UniqueIDGenerator(
			"leave");

	public LeaveManagementService(IDatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}

	@Override
	public IUserLeaveRequest applyForLeave(String userId,
			String approvingUserId,
			Map<LeaveType, Integer> leaveTypeVsLeaveCount)
			throws LeaveManagementServiceException {
		Date currentTime = new Date();
		UserLeaveRequestEntity userLeaveRequestEntity = new UserLeaveRequestEntity();
		userLeaveRequestEntity.setApprovingUserId(approvingUserId);
		userLeaveRequestEntity.setLeaveApplicationId(leaveRequestIdGenerator
				.getNextUID(userId));
		userLeaveRequestEntity.setUserId(userId);
		userLeaveRequestEntity.setLeaveTypeVsLeaveCount(leaveTypeVsLeaveCount);
		userLeaveRequestEntity.setDateAdded(currentTime);
		userLeaveRequestEntity.setDateModified(currentTime);
		try {
			databaseManager.save(userLeaveRequestEntity);
			System.out.println(userLeaveRequestEntity);
			return userLeaveRequestEntity;
		} catch (DatabaseManagerException e) {
			logger.error("error while adding user leave request", e);
			throw new LeaveManagementServiceException(e.getMessage(), e);
		}
	}

	@Override
	public IUserLeaveRequest updateLeaveRequestStatus(
			String leaveApplicationId, LeaveRequestStatus leaveStatus)
			throws LeaveManagementServiceException {
		// quota decrement
		try {
			Date currentTime = new Date();
			UserLeaveRequestEntity userLeaveRequestEntity = databaseManager
					.get(UserLeaveRequestEntity.class, leaveApplicationId);
			userLeaveRequestEntity.setLeaveApplicationStatus(leaveStatus);
			userLeaveRequestEntity.setDateModified(currentTime);
			databaseManager.save(userLeaveRequestEntity);
			System.out.println(userLeaveRequestEntity);
			return userLeaveRequestEntity;
		} catch (DatabaseManagerException | EntityNotFoundException e) {
			logger.error("error while adding user leave request", e);
			throw new LeaveManagementServiceException(e.getMessage(), e);
		}

	}

	@Override
	public IUserLeaveQuota getUserLeaveQuota(String userId)
			throws LeaveManagementServiceException {
		try {
			UserLeaveQuotaEntity userLeaveQuotaEntity = databaseManager.get(
					UserLeaveQuotaEntity.class, userId);
			return userLeaveQuotaEntity;
		} catch (DatabaseManagerException | EntityNotFoundException e) {
			logger.error("error while fetching User Leave Quota", e);
			throw new LeaveManagementServiceException(e.getMessage(), e);
		}
	}

	@Override
	public IUserRoleLeaveQuota updateLeaveQuotaForRole(UserRole userRole,
			Map<LeaveType, Integer> leaveTypeVsLeaveCount)
			throws LeaveManagementServiceException {

		// TODO
		try {
			Date currentTime = new Date();
			UserRoleLeaveQuotaEntity userRoleLeaveQuotaEntity = databaseManager
					.get(UserRoleLeaveQuotaEntity.class, userRole);
			userRoleLeaveQuotaEntity
					.setLeaveTypeVsLeaveCount(leaveTypeVsLeaveCount);
			userRoleLeaveQuotaEntity.setDateModified(currentTime);
			databaseManager.save(userRoleLeaveQuotaEntity);
			return userRoleLeaveQuotaEntity;
		} catch (DatabaseManagerException | EntityNotFoundException e) {
			logger.error("error while adding user leave request", e);
			throw new LeaveManagementServiceException(e.getMessage(), e);
		}
	}

	@Override
	public List<? extends IUserLeaveRequest> getLeaveRequestsByUserId(
			String userId, LeaveRequestStatus leaveRequestStatus)
			throws LeaveManagementServiceException {
		try {
			QUserLeaveRequestEntity qUserLeaveRequestEntity = QUserLeaveRequestEntity.userLeaveRequestEntity;
			HibernateQuery hibernateQuery = new HibernateQuery()
					.from(qUserLeaveRequestEntity)
					.where(qUserLeaveRequestEntity.userId
							.eq(userId)
							.and(qUserLeaveRequestEntity.leaveApplicationStatus.eq(leaveRequestStatus)));

			List<UserLeaveRequestEntity> executeQueryAndGetResults = databaseManager
					.executeQueryAndGetResults(hibernateQuery,
							qUserLeaveRequestEntity);
			return executeQueryAndGetResults;
		} catch (DatabaseManagerException e) {
			logger.error("Error while retrieving Leave Request by User Id",
					e.getMessage(), e);
			throw new LeaveManagementServiceException(e.getMessage(), e);
		}

	}

	@Override
	public List<? extends IUserLeaveRequest> getAllPendingLeaveRequestsByLeaveStatus(
			String approvingUserId, LeaveRequestStatus leaveStatus)
			throws LeaveManagementServiceException {

		try {
			QUserLeaveRequestEntity qUserLeaveRequestEntity = QUserLeaveRequestEntity.userLeaveRequestEntity;
			HibernateQuery hibernateQuery = new HibernateQuery().from(
					qUserLeaveRequestEntity).where(
					qUserLeaveRequestEntity.approvingUserId.eq(approvingUserId)
							.and(qUserLeaveRequestEntity.leaveApplicationStatus
									.eq(leaveStatus)));

			List<UserLeaveRequestEntity> executeQueryAndGetResults = databaseManager
					.executeQueryAndGetResults(hibernateQuery,
							qUserLeaveRequestEntity);
			return executeQueryAndGetResults;
		} catch (DatabaseManagerException e) {
			logger.error("Error while retrieving Leave Request by User Id",
					e.getMessage(), e);
			throw new LeaveManagementServiceException(e.getMessage(), e);
		}
	}
}