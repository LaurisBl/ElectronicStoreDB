package lt.viko.eif.lblauzdys.electronicsstore.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import java.text.DecimalFormat;

/**
 * Computer class holds information about the computers held in the shelves of a store.
 *
 * @author laurynas.blauzdys@stud.viko.lt
 * @see Shelf
 * @see Store
 * @since 1.0
 */

@Entity
@Table(name = "computers")
public class Computer {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private int id;
	private String Name;
	private String Manufacturer;
	private String UseCase;
	private String Specification;
	private float Price;

	public Computer(String name, String manufacturer, String useCase, String specification, float price) {
		Name = name;
		Manufacturer = manufacturer;
		UseCase = useCase;
		Specification = specification;
		Price = price;
	}

	public Computer() {
	}

	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat();
		df.setMinimumFractionDigits(2);

		return String.format("\t\t%s:\n" +
						"\t\t\tManufacturer: %s\n" +
						"\t\t\tUse case: %s\n" +
						"\t\t\tSpecification: %s\n" +
						"\t\t\tPrice: %s\n",
				this.Name,
				this.Manufacturer,
				this.UseCase,
				this.Specification,
				df.format(this.Price));
	}

	@XmlAttribute(name = "Name")
	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	@XmlAttribute(name = "Manufacturer")
	public String getManufacturer() {
		return Manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		Manufacturer = manufacturer;
	}

	@XmlAttribute(name = "UseCase")
	public String getUseCase() {
		return UseCase;
	}

	public void setUseCase(String useCase) {
		UseCase = useCase;
	}

	@XmlAttribute(name = "Specification")
	public String getSpecification() {
		return Specification;
	}

	public void setSpecification(String specification) {
		Specification = specification;
	}

	@XmlAttribute(name = "Price")
	public float getPrice() {
		return Price;
	}

	public void setPrice(float price) {
		Price = price;
	}
}
