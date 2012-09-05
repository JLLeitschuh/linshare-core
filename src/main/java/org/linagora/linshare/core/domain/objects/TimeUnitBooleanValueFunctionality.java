
package org.linagora.linshare.core.domain.objects;

import org.linagora.linshare.core.domain.entities.TimeUnitClass;
import org.linagora.linshare.core.domain.entities.UnitBooleanValueFunctionality;

/**
 * This class is just for easy use. It is not an entity
 * @author fred
 *
 */
public class TimeUnitBooleanValueFunctionality extends UnitBooleanValueFunctionality{

	public TimeUnitBooleanValueFunctionality(UnitBooleanValueFunctionality f) {
		super();
		setActivationPolicy(f.getActivationPolicy());
		setConfigurationPolicy(f.getConfigurationPolicy());
		setDomain(f.getDomain());
		setId(f.getId());
		setIdentifier(f.getIdentifier());
		setSystem(f.isSystem());
		setUnit(f.getUnit());
		setValue(f.getValue());
		setBool(f.isBool());
	}
	
	public int toCalendarValue() {
		TimeUnitClass timeUnit = (TimeUnitClass)getUnit();
		return timeUnit.toCalendarValue();
	}
}
