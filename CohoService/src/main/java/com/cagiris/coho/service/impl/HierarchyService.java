/*
 * Copyright (c) 2015, Cagiris Pvt. Ltd.
 * All rights reserved.
 */
package com.cagiris.coho.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cagiris.coho.service.api.AuthenicationPolicy;
import com.cagiris.coho.service.api.IHierarchyService;
import com.cagiris.coho.service.api.IOrganization;
import com.cagiris.coho.service.api.ITeam;
import com.cagiris.coho.service.api.ITeamUser;
import com.cagiris.coho.service.api.IUser;
import com.cagiris.coho.service.api.UserRole;
import com.cagiris.coho.service.db.api.DatabaseManagerException;
import com.cagiris.coho.service.db.api.EntityNotFoundException;
import com.cagiris.coho.service.db.api.IDatabaseManager;
import com.cagiris.coho.service.db.impl.CohoDeleteClause;
import com.cagiris.coho.service.entity.OrganizationEntity;
import com.cagiris.coho.service.entity.QOrganizationEntity;
import com.cagiris.coho.service.entity.QTeamEntity;
import com.cagiris.coho.service.entity.QTeamUserEntity;
import com.cagiris.coho.service.entity.TeamEntity;
import com.cagiris.coho.service.entity.TeamUserEntity;
import com.cagiris.coho.service.entity.UserEntity;
import com.cagiris.coho.service.exception.HierarchyServiceException;
import com.cagiris.coho.service.exception.ResourceNotFoundException;
import com.mysema.query.jpa.hibernate.HibernateQuery;

/**
 *
 * @author: ssnk
 */

public class HierarchyService implements IHierarchyService {

	private static final Logger logger = LoggerFactory.getLogger(HierarchyService.class);

	private IDatabaseManager databaseManager;

	public HierarchyService(IDatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}

	public void createDefaultHierarchy() {
		try {
			IOrganization defaultOrganization = addOrganization("Coho", "Default Organization");
			ITeam defaultTeam = addTeam(defaultOrganization.getOrganizationId(), null, "Default Team", "Default team");
			addUserToTeam(defaultTeam.getTeamId(), "agent", "agent", "agent", UserRole.AGENT,
					AuthenicationPolicy.PASSWORD_BASED);
			addUserToTeam(defaultTeam.getTeamId(), "supervisor", "supervisor", "supervisor", UserRole.SUPERVISOR,
					AuthenicationPolicy.PASSWORD_BASED);
			addUserToTeam(defaultTeam.getTeamId(), "admin", "admin", "admin", UserRole.ADMIN,
					AuthenicationPolicy.PASSWORD_BASED);
			addUserToTeam(defaultTeam.getTeamId(), "root", "root", "root", UserRole.ROOT,
					AuthenicationPolicy.PASSWORD_BASED);
		} catch (HierarchyServiceException e) {
			logger.error("Failed to create default hierarchy, error: {}", e.getMessage());
		}
	}

	@Override
	public ITeam addTeam(Long organizationId, Long parentTeamId, String teamName, String teamDescription)
			throws HierarchyServiceException {
		Date currentTime = new Date(System.currentTimeMillis());
		TeamEntity teamEntity = new TeamEntity();
		teamEntity.setTeamName(teamName);
		teamEntity.setTeamDescription(teamDescription);
		teamEntity.setDateAdded(currentTime);
		teamEntity.setDateModified(currentTime);
		OrganizationEntity organizationEntity;
		try {
			organizationEntity = databaseManager.get(OrganizationEntity.class, organizationId);
			if (parentTeamId != null) {
				TeamEntity parentTeamEntity = databaseManager.get(TeamEntity.class, parentTeamId);
				teamEntity.setParentTeamEntity(parentTeamEntity);
			}
			teamEntity.setOrganizationEntity(organizationEntity);
			databaseManager.save(teamEntity);
		} catch (DatabaseManagerException | EntityNotFoundException e) {
			logger.error("Error while adding team:{}", e.getMessage(), e);
			throw new HierarchyServiceException(e);
		}
		return teamEntity;
	}

	@Override
	public void deleteTeam(Long teamId) throws HierarchyServiceException {
		try {
			TeamEntity teamEntity = databaseManager.get(TeamEntity.class, teamId);
			databaseManager.delete(teamEntity);
		} catch (DatabaseManagerException | EntityNotFoundException e) {
			logger.error("Error while deleting team:{}", e.getMessage(), e);
			throw new HierarchyServiceException(e);
		}
	}

	@Override
	public ITeam getTeam(Long teamId) throws HierarchyServiceException, ResourceNotFoundException {
		try {
			return databaseManager.get(TeamEntity.class, teamId);
		} catch (DatabaseManagerException e) {
			throw new HierarchyServiceException(e);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(e.getMessage(), e);
		}
	}

	@Override
	public List<? extends ITeam> getAllTeams(Long organizationId) throws HierarchyServiceException {
		QTeamEntity qTeamEntity = QTeamEntity.teamEntity;
		HibernateQuery hibernateQuery = new HibernateQuery().from(qTeamEntity).where(
				qTeamEntity.organizationEntity.organizationId.eq(organizationId));
		try {
			return databaseManager.executeQueryAndGetResults(hibernateQuery, qTeamEntity);
		} catch (DatabaseManagerException e) {
			throw new HierarchyServiceException(e);
		}
	}

	@Override
	public List<? extends ITeam> getAllSubTeams(Long parentTeamId) throws HierarchyServiceException {
		QTeamEntity qTeamEntity = QTeamEntity.teamEntity;
		QTeamEntity parentQTeamEntity = qTeamEntity.parentTeamEntity;
		HibernateQuery hibernateQuery = new HibernateQuery().from(qTeamEntity).where(
				parentQTeamEntity.teamId.ne(-1l).and(parentQTeamEntity.teamId.eq(parentTeamId)));
		try {
			return databaseManager.executeQueryAndGetResults(hibernateQuery, qTeamEntity);
		} catch (DatabaseManagerException e) {
			throw new HierarchyServiceException(e);
		}
	}

	@Override
	public IOrganization getOrganizationInfo(Long organizationId) throws HierarchyServiceException,
			ResourceNotFoundException {
		try {
			return databaseManager.get(OrganizationEntity.class, organizationId);
		} catch (DatabaseManagerException e) {
			throw new HierarchyServiceException(e);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(e.getMessage(), e);
		}
	}

	@Override
	public ITeamUser addUserToTeam(Long teamId, String userId, String userName, String authToken, UserRole userRole,
			AuthenicationPolicy authenicationPolicy) throws HierarchyServiceException {
		Date currentTime = new Date(System.currentTimeMillis());
		UserEntity userEntity = new UserEntity();
		userEntity.setAuthPolicy(authenicationPolicy);
		userEntity.setUserId(userId);
		userEntity.setAuthToken(authToken);
		userEntity.setUserRole(userRole);
		userEntity.setUserName(userName);
		userEntity.setDateAdded(currentTime);
		userEntity.setDateModified(currentTime);
		TeamUserEntity teamUserEntity = new TeamUserEntity();
		teamUserEntity.setUserEntity(userEntity);
		try {
			TeamEntity teamEntity = databaseManager.get(TeamEntity.class, teamId);
			databaseManager.save(userEntity);
			userEntity = databaseManager.get(UserEntity.class, userId);
			teamUserEntity.setTeamEntity(teamEntity);
			teamUserEntity.setUserEntity(userEntity);
			databaseManager.save(teamUserEntity);
			return teamUserEntity;
		} catch (DatabaseManagerException | EntityNotFoundException e) {
			logger.error("Error while adding team user:{}", e.getMessage(), e);
			throw new HierarchyServiceException(e);
		}
	}

	@Override
	public ITeamUser assignUserToTeam(Long teamId, String userId) throws HierarchyServiceException {
		UserEntity userEntity;
		try {
			userEntity = databaseManager.get(UserEntity.class, userId);
		} catch (DatabaseManagerException | EntityNotFoundException e1) {
			logger.error("Error while fetching user:{}", e1.getMessage(), e1);
			throw new HierarchyServiceException(e1);
		}
		TeamUserEntity teamUserEntity = new TeamUserEntity();
		teamUserEntity.setUserEntity(userEntity);
		try {
			TeamEntity teamEntity = databaseManager.get(TeamEntity.class, teamId);
			teamUserEntity.setTeamEntity(teamEntity);
			databaseManager.save(teamUserEntity);
			return teamUserEntity;
		} catch (DatabaseManagerException | EntityNotFoundException e) {
			logger.error("Error while adding team user:{}", e.getMessage(), e);
			throw new HierarchyServiceException(e);
		}
	}

	@Override
	public void removeUserFromTeam(Long teamId, String userId) throws HierarchyServiceException {
		QTeamUserEntity qTeamUserEntity = QTeamUserEntity.teamUserEntity;
		CohoDeleteClause hibernateDeleteClause = new CohoDeleteClause(qTeamUserEntity)
				.where(qTeamUserEntity.userEntity.userId.eq(userId).and(qTeamUserEntity.teamEntity.teamId.eq(teamId)));
		long deletedUserCount;
		try {
			deletedUserCount = databaseManager.executeDeleteClause(hibernateDeleteClause);
			logger.info("No of users deleted:{}", deletedUserCount);
		} catch (DatabaseManagerException e) {
			throw new HierarchyServiceException(e);
		}
	}

	@Override
	public List<? extends ITeamUser> getAllUsersForTeam(Long teamId) throws HierarchyServiceException {
		QTeamUserEntity qTeamUserEntity = QTeamUserEntity.teamUserEntity;
		HibernateQuery hibernateQuery = new HibernateQuery().from(qTeamUserEntity).where(
				qTeamUserEntity.teamEntity.teamId.eq(teamId));
		try {
			return databaseManager.executeQueryAndGetResults(hibernateQuery, qTeamUserEntity);
		} catch (DatabaseManagerException e) {
			throw new HierarchyServiceException(e);
		}
	}

	@Override
	public List<? extends ITeamUser> getAllUsersForTeamByRole(Long teamId, UserRole userRole)
			throws HierarchyServiceException {
		QTeamUserEntity qTeamUserEntity = QTeamUserEntity.teamUserEntity;
		HibernateQuery hibernateQuery = new HibernateQuery();
		hibernateQuery.from(qTeamUserEntity).where(
				qTeamUserEntity.teamEntity.teamId.eq(teamId).and(qTeamUserEntity.userEntity.userRole.eq(userRole)));
		try {
			return databaseManager.executeQueryAndGetResults(hibernateQuery, qTeamUserEntity);
		} catch (DatabaseManagerException e) {
			throw new HierarchyServiceException(e);
		}
	}

	@Override
	public void deleteUser(String userId) throws HierarchyServiceException {
	}

	@Override
	public List<? extends ITeam> getTeamsForUser(String userId) throws HierarchyServiceException {
		QTeamUserEntity qTeamUserEntity = QTeamUserEntity.teamUserEntity;
		HibernateQuery hibernateQuery = new HibernateQuery().from(qTeamUserEntity).where(
				qTeamUserEntity.userEntity.userId.eq(userId));
		List<ITeam> teams = new ArrayList<ITeam>();
		try {
			List<TeamUserEntity> teamUsers = databaseManager.executeQueryAndGetResults(hibernateQuery, qTeamUserEntity);
			for (TeamUserEntity teamUserEntity : teamUsers) {
				TeamEntity teamEntity = databaseManager.get(TeamEntity.class, teamUserEntity.getTeamId());
				teams.add(teamEntity);
			}
		} catch (DatabaseManagerException | EntityNotFoundException e) {
			throw new HierarchyServiceException(e);
		}
		return teams;
	}

	@Override
	public IOrganization addOrganization(String organizationName, String organizationDescription)
			throws HierarchyServiceException {
		Date currentTime = new Date(System.currentTimeMillis());
		OrganizationEntity organizationEntity = new OrganizationEntity();
		organizationEntity.setOrganizationName(organizationName);
		organizationEntity.setOrganizationDescription(organizationDescription);
		organizationEntity.setDateAdded(currentTime);
		organizationEntity.setDateModified(currentTime);
		try {
			databaseManager.save(organizationEntity);
		} catch (DatabaseManagerException e) {
			logger.error("Error while adding organization:{}", e.getMessage(), e);
			throw new HierarchyServiceException(e);
		}
		return organizationEntity;
	}

	@Override
	public IUser getUser(String userId) throws HierarchyServiceException, ResourceNotFoundException {
		try {
			UserEntity userEntity = databaseManager.get(UserEntity.class, userId);
			return userEntity;
		} catch (DatabaseManagerException e) {
			logger.error("Error while fetching user:{}", userId, e);
			throw new HierarchyServiceException(e);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(e.getMessage(), e);
		}
	}

	@Override
	public List<? extends IOrganization> getAllOrganizationInfo()
			throws HierarchyServiceException, ResourceNotFoundException {
		QOrganizationEntity qOrganizationEntity = QOrganizationEntity.organizationEntity;
		HibernateQuery hibernateQuery = new HibernateQuery().from(qOrganizationEntity);
		try {
			return databaseManager.executeQueryAndGetResults(hibernateQuery, qOrganizationEntity);
		} catch (DatabaseManagerException e) {
			throw new HierarchyServiceException(e);
		}
	}

}
