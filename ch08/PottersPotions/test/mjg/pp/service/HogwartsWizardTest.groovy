package mjg.pp.service
;

import javax.xml.namespace.QName 
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service 

import mjg.pp.entities.Cauldron;
import mjg.pp.entities.Ingredient;
import mjg.pp.entities.Potion;
import mjg.pp.service.Granger;
import mjg.pp.service.HogwartsWizard;
import mjg.pp.service.Wizard;

import spock.lang.Shared;
import spock.lang.Specification;

class HogwartsWizardTest extends Specification {
	@Shared Endpoint e
	Wizard w
	Cauldron c
	
	def setupSpec() {
		e = Endpoint.publish("http://localhost:1234/wizard", 
			new HogwartsWizard(wizard:new Granger()))
	}
	
	def setup() {
		Service service = Service.create(
			new URL('http://localhost:1234/wizard?WSDL'),
			new QName('http://service.pp.mjg/','HogwartsWizardService'))
		w = service.getPort(
			new QName('http://service.pp.mjg/','HogwartsWizardPort'),
			Wizard.class)
		c = new Cauldron()
		c << new Ingredient(name:'sopophorous bean',amount:1,units:'bean') <<
		  new Ingredient(name:'gillyleaf',amount:1,units:'handful')
	}
	
	def "wizard exposed as a web service"() {
		expect: w
	}
	
	def "brew a potion"() {
		when: Potion p = w.brewPotion(c)
		then: 
		p != null
		p.ingredients*.name.containsAll 'sopophorous bean','gillyleaf'
	}
	
	def "check for dark arts disclaimer"() {
		setup:
		c << new Ingredient(name:'unicorn blood',amount:2,units:'smidges')
		
		when:
		Potion p = w.brewPotion(c)
		
		then:
		p.effect == 'Possible dark arts detected. No warranty expressed or implied'
	}
	
	def cleanupSpec() {
		e?.stop()
	}
}
