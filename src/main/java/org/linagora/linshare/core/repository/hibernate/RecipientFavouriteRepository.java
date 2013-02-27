/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.repository.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.RecipientFavourite;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.LinShareNotSuchElementException;
import org.linagora.linshare.core.repository.FavouriteRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class RecipientFavouriteRepository extends AbstractRepositoryImpl<RecipientFavourite> implements FavouriteRepository<String,User, RecipientFavourite> {

	public RecipientFavouriteRepository(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	public boolean existFavourite(User owner, String element) {
		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class);
		det.add(Restrictions.eq("owner", owner));
		det.add(Restrictions.eq("recipient", element));
		List<RecipientFavourite> listElement=findByCriteria(det);


		return (listElement!=null && !listElement.isEmpty());	

	}

	public String getElementWithMaxWeight(User u) throws LinShareNotSuchElementException {

		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class);
		det.add(Restrictions.eq("owner", u));
		det.addOrder(Order.desc("weight"));
		List<RecipientFavourite> listElement=findByCriteria(det);


		if(listElement==null || listElement.isEmpty() ){
			throw new LinShareNotSuchElementException("the owner has no recipient associated ");
		}

		return listElement.get(0).getRecipient();
	}


	public List<String> getElementsOrderByWeight(User u) {
		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class);
		det.add(Restrictions.eq("owner", u));
		det.addOrder(Order.asc("weight"));
		List<RecipientFavourite> listElement=findByCriteria(det);

		return transformRecipientFavouriteToRecipient(listElement);
	}

	public List<String> getElementsOrderByWeightDesc(User u) {
		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class);
		det.add(Restrictions.eq("owner", u));
		det.addOrder(Order.desc("weight"));

		List<RecipientFavourite> listElement=findByCriteria(det);

		return transformRecipientFavouriteToRecipient(listElement);
	}

	public Long getWeight(String element,User u)  throws LinShareNotSuchElementException {
		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class);
		det.add(Restrictions.eq("owner", u));
		det.add(Restrictions.eq("recipient", element));
		List<RecipientFavourite> listElement=findByCriteria(det);
		if(listElement==null || listElement.isEmpty() ){
			throw new LinShareNotSuchElementException("the owner has no recipient associated ");
		}
		return listElement.get(0).getWeight();
	}

	public void inc(String element,User u)  throws LinShareNotSuchElementException,BusinessException {
		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class);
		det.add(Restrictions.eq("owner", u));
		det.add(Restrictions.eq("recipient", element));
		List<RecipientFavourite> listElement=findByCriteria(det);
		if(listElement==null || listElement.isEmpty() ){
			throw new LinShareNotSuchElementException("the owner has no recipient associated ");
		}
		RecipientFavourite favourite=listElement.get(0);
		favourite.inc();
		super.update(favourite);

	}

	public void inc(List<String> element,User u)  throws LinShareNotSuchElementException,BusinessException {
		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class);
		det.add(Restrictions.eq("owner", u));
		det.add(Restrictions.in("recipient", element));
		List<RecipientFavourite> listElement=findByCriteria(det);
		if(listElement==null || listElement.isEmpty() ){
			throw new LinShareNotSuchElementException("the owner has no recipient associated ");
		}
		if(listElement.size()!=element.size()){
			throw new LinShareNotSuchElementException("one of the recipient is not present !");
		}
		for(RecipientFavourite recipientFavourite: listElement){
			recipientFavourite.inc();
			update(recipientFavourite);
		}

	}


	public void incAndCreate(List<String> elements,User u)   throws LinShareNotSuchElementException,BusinessException{
		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class);
		det.add(Restrictions.eq("owner", u));
		det.add(Restrictions.in("recipient", elements));

		List<RecipientFavourite> recipients=findByCriteria(det);

		/**
		 * Create favourite when the favourite doesn't exist in the database for the current owner.
		 */
		createFavourite(elements, recipients, u);

		/**
		 * Increment and update others.
		 */
		
		
		for(RecipientFavourite recipientFavourite: recipients){
			recipientFavourite.inc();
			update(recipientFavourite);
		}
	}

	private List<String> transformRecipientFavouriteToRecipient(List<RecipientFavourite> recipients){
		ArrayList<String> listElements=new ArrayList<String>();
		if(null!=recipients){
			for(RecipientFavourite recipientFavourite: recipients){
				listElements.add(recipientFavourite.getRecipient());
			}
		}
		return listElements;
	}

	private void createFavourite(List<String> elements,List<RecipientFavourite> recipients,User u) throws BusinessException{
		for(String recipient:elements){
			boolean contain=false;
			for(RecipientFavourite recipientFavour: recipients){
				if(recipientFavour.getRecipient().equals(recipient)){
					contain=true;
					break;
				}
			}
			if(!contain){
				super.create(new RecipientFavourite(u,recipient));
			}
		}
	}
	
	private List<String> reorderElement(List<String> elements,List<RecipientFavourite> recipients){
		
		ArrayList<String> orderedElements=new ArrayList<String>();

		//1) first put favorite at the top of the list
		for(RecipientFavourite recipientFavour: recipients){
			orderedElements.add(recipientFavour.getRecipient());
		}
		
		//2) second put remaining entries in the list (exclude them if they are present in favorite)
		for(String element:elements){
			if(!orderedElements.contains(element)){
				orderedElements.add(element);
			}
		}
		return orderedElements;
	}
	
	@Override
	protected DetachedCriteria getNaturalKeyCriteria(RecipientFavourite entity) {
		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class).add(
				Restrictions.eq("owner", entity.getOwner())).add(
						Restrictions.eq("recipient", entity.getRecipient()));
		return det;
	}

	public List<String> reorderElementsByWeightDesc(List<String> elements, User owner)
			{
		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class);
		det.add(Restrictions.eq("owner", owner));
		det.add(Restrictions.in("recipient", elements));
		det.addOrder(Order.desc("weight"));
		List<RecipientFavourite> recipients=findByCriteria(det);
		
		return reorderElement(elements, recipients);
		
	}

	public List<String> findMatchElementsOrderByWeight(String matchStartWith, User owner) {
		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class);
		det.add(Restrictions.eq("owner", owner));
		det.add(Restrictions.ilike("recipient", matchStartWith,MatchMode.START));
		det.addOrder(Order.desc("weight"));
		List<RecipientFavourite> recipients=findByCriteria(det);
		
		ArrayList<String> mails=new ArrayList<String>();
		
		for(RecipientFavourite recipientFavour: recipients){
			mails.add(recipientFavour.getRecipient());
		}
		return mails;
	}

	public void deleteFavoritesOfUser(User owner) throws IllegalArgumentException, BusinessException {
		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class);
		det.add(Restrictions.eq("owner", owner));
		List<RecipientFavourite> recipients=findByCriteria(det);
		for (RecipientFavourite recipientFavourite : recipients) {
			delete(recipientFavourite);
		}
	}
}
