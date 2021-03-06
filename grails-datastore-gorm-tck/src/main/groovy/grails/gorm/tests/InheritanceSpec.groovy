package grails.gorm.tests

import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: graemerocher
 * Date: Aug 30, 2010
 * Time: 12:53:23 PM
 * To change this template use File | Settings | File Templates.
 */
class InheritanceSpec extends GormDatastoreSpec {

    void "Test querying with inheritance"() {

      given:
        def city = new City([code: "LON", name: "London", longitude: 49.1, latitude: 53.1])
        def location = new Location([code: "XX", name: "The World"])
        def country = new Country([code: "UK", name: "United Kingdom", population: 10000000])

        country.save()
        city.save()
        location.save()

        City.withSession { session -> session.flush();session.clear() }

      when:
        city = City.get(city.id)
        def london = Location.get(city.id)
        country = Location.findByName("United Kingdom")
        def london2 = Location.findByName("London")

      then:
        1 == City.count()
        1 == Country.count()
        3 == Location.count()



        city != null
        city instanceof City
        london instanceof City
        london2 instanceof City
        "London" == london2.name
        49.1 == london2.longitude
        "LON" == london2.code

        country instanceof Country
        "UK" == country.code
        10000000 == country.population

    }

  def clearSession() {
    City.withSession { session -> session.flush(); }
  }
}

class Location {
  Long id
  String name
  String code

  static mapping = {
    name index:true
  }
}
class City extends Location {
    BigDecimal latitude
    BigDecimal longitude
}

class Country extends Location {
  Integer population
}
