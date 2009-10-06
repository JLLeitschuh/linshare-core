/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.view.tapestry.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.ioc.annotations.Inject;


@SupportsInformalParameters
public class ConfirmPopup {


	/* ***********************************************************
     *                         Parameters
     ************************************************************ */
	
	@SuppressWarnings("unused")
	@Parameter(required=true,defaultPrefix=BindingConstants.LITERAL)
	@Property
	private String messageLabel;
	
	@SuppressWarnings("unused")
	@Parameter(required=true,defaultPrefix=BindingConstants.LITERAL)
	private String eventName;
	
	
	@Component(parameters = {"style=bluelighting", "show=false","width=500", "height=100"})
	private WindowWithEffects window_confirm;
	
	
	
    /* ***********************************************************
     *                      Injected services
     ************************************************************ */
		

	@Inject
	private ComponentResources componentResources;
	
	
    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
	
	public void onActionFromConfirm(){
		componentResources.getContainer().getComponentResources().triggerEvent(eventName, null,null);
	}
	

	
	public String getJSONId() {
    	return window_confirm.getJSONId();
    }
}
