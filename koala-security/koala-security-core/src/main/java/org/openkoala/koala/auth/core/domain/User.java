package org.openkoala.koala.auth.core.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.NoResultException;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.openkoala.koala.auth.core.domain.infra.jpa.UserRepository;

import com.dayatang.domain.InstanceFactory;
import com.dayatang.utils.DateUtils;

/**
 * 用户实体类
 * 
 * @author lingen
 * 
 */

@Entity
@DiscriminatorColumn(name = "User", discriminatorType = DiscriminatorType.STRING)
public class User extends Identity {

	private static final long serialVersionUID = 1828900234948658820L;

	@Column(name = "LAST_LOGIN_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLoginTime;

	@Column(name = "USER_ACCOUNT")
	private String userAccount;

	@Column(name = "USER_PASSWORD")
	private String userPassword;

	@Column(name = "USER_DESC")
	private String userDesc;

	@Column(name = "LAST_MODIFY_TIME")
	private Date lastModifyTime;

	public Set<RoleUserAuthorization> getRoles() {
		return new HashSet<RoleUserAuthorization>(RoleUserAuthorization.findAuthorizationByUser(this));
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getUserDesc() {
		return userDesc;
	}

	public void setUserDesc(String userDesc) {
		this.userDesc = userDesc;
	}

	public Date getLastModifyTime() {
		return lastModifyTime;
	}

	public void setLastModifyTime(Date lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		return true;
	}

	/* 定义领域接口 */
	static UserRepository getUserRepository() {
		return InstanceFactory.getInstance(UserRepository.class);
	}

	public static User findByUserAccount(String userAccount) {
		try {
			return User.getRepository().getSingleResult(
					"select m from org.openkoala.koala.auth.core.domain.User m where m.userAccount=?1",
					new Object[] { userAccount }, User.class);
		} catch (NoResultException e) {
			return null;
		}
	}

	public void AssignRole(Role role) {
		RoleUserAuthorization roleUserAssignment = new RoleUserAuthorization();
		roleUserAssignment.setAbolishDate(DateUtils.MAX_DATE);
		roleUserAssignment.setCreateDate(new Date());
		roleUserAssignment.setScheduledAbolishDate(new Date());
		roleUserAssignment.setRole(role);
		roleUserAssignment.setUser(this);
		roleUserAssignment.save();
	}

	public void AssignRole(List<Role> roles) {
		for (Role role : roles) {
			RoleUserAuthorization roleUserAssignment = new RoleUserAuthorization();
			roleUserAssignment.setAbolishDate(new Date());
			roleUserAssignment.setCreateDate(new Date());
			roleUserAssignment.setRole(role);
			roleUserAssignment.setUser(this);
			roleUserAssignment.save();
		}
	}
	
	/**
	 * 用户账号是否存在
	 * @return
	 */
	public boolean isAccountExist() {
		return !findByNamedQuery("isAccountExist", new Object[] { getUserAccount(), new Date() }, User.class).isEmpty();
	}

	public void abolishRole(Role role) {
		List<RoleUserAuthorization> authorizations = RoleUserAuthorization.getRepository().find( //
				"select m from RoleUserAuthorization m where m.user.id=? and " //
				+ " m.role.id=? and m.abolishDate>?", new Object[] { this.getId(), role.getId(), new Date() },  //
				RoleUserAuthorization.class);
		for (RoleUserAuthorization authorization : authorizations) {
			authorization.setAbolishDate(new Date());
		}

	}
	
	public void passwordReset() {
		this.setUserPassword("abcd");
		this.save();
	}

}