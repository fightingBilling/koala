package org.openkoala.security.core.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.dayatang.utils.Assert;
import org.openkoala.security.core.NameIsExistedException;

/**
 * 可授权实体，代表某种权限（Permission）或权限集合（Role），可被授予Actor。
 * 
 * @author luzhao
 * 
 */
@Entity
@Table(name = "KS_AUTHORITIES")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "CATEGORY", discriminatorType = DiscriminatorType.STRING)
public abstract class Authority extends SecurityAbstractEntity {

	private static final long serialVersionUID = -5570169700634882013L;

	/**
	 * 名称[用于判断存储中是否已经存在]
	 */
	@Column(name = "NAME")
	private String name;

	/**
	 * 描述
	 */
	@Column(name = "DESCRIPTION")
	private String description;

	@ManyToMany
	@JoinTable(name = "KS_AS_MAP", //
	joinColumns = @JoinColumn(name = "AUTHORITY_ID"), //
	inverseJoinColumns = @JoinColumn(name = "SECURITYRESOURCE_ID"))
	private Set<SecurityResource> securityResources = new HashSet<SecurityResource>();

	Authority() {
	}

	public Authority(String name) {
//		Assert.isBlank(name, "名称不能为空");
		this.name = name;
	}

	public void addSecurityResource(SecurityResource... securityResource) {
		this.securityResources.addAll(Arrays.asList(securityResource));
	}

	public static Set<MenuResource> findMenuResourceByAuthorities(Set<? extends Authority> authorities) {
		Set<MenuResource> results = new HashSet<MenuResource>();

		for (Authority authority : authorities) {
			results.addAll(findMenuResourceByAuthority(authority));
		}

		return results;
	}

	public static Set<MenuResource> findMenuResourceByAuthority(Authority authority) {
		Set<MenuResource> results = new HashSet<MenuResource>();
		if (authority instanceof Role) {
			Role role = (Role) authority;
			Set<Permission> permissions = role.getPermissions();
			results.addAll(findMenuResourceByAuthorities(permissions));
		}
		Set<SecurityResource> securityResources = authority.getSecurityResources();
		for (SecurityResource securityResource : securityResources) {
			if (securityResource instanceof MenuResource) {
				results.add((MenuResource) securityResource);
			}
		}
		return results;
	}

	public static Set<MenuResource> findTopMenuResourceByAuthority(Authority authority) {
		List<MenuResource> menuResources = getRepository().createNamedQuery("findTopMenuResourceByAuthority")//
				.addParameter("authorityId", authority.getId())//
				.list();

		return new HashSet<MenuResource>(menuResources);
	}

	public Authority getAuthorityBy(String name) {
		Authority authority = getRepository().createCriteriaQuery(Authority.class)//
				.eq("name", this.name)//
				.singleResult();
		return authority != null ? authority : null;
	}

	@Override
	public final void save() {
		// isExisted();
		super.save();
	}

	protected void isExisted() {
		if (isExistName(this.name)) {
			throw new NameIsExistedException("name.exist");
		}
	}

	private boolean isExistName(String name) {
		return getAuthorityBy(name) != null;
	}

	public abstract void update();

	@Override
	public void remove() {
		for (Authorization authorization : Authorization.findByAuthority(this)) {
			authorization.remove();
		}
		super.remove();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<SecurityResource> getSecurityResources() {
		return Collections.unmodifiableSet(securityResources);
	}

	public void setSecurityResources(Set<SecurityResource> securityResources) {
		this.securityResources = securityResources;
	}

	@Override
	public String toString() {
		return "Authority [name=" + name + ", description=" + description + "]";
	}

	@Override
	public String[] businessKeys() {
		return new String[] { "name", "description" };
	}
}