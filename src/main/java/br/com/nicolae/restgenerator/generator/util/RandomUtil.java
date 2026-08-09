package br.com.nicolae.restgenerator.generator.util;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import net.datafaker.Faker;

public class RandomUtil {
	
	public static Faker faker = new Faker(new Locale("pt", "BR"));

	public static String randomFullName() {
		return faker.name().fullName().replace(".", "");
	}

	public static String randomFirstName() {
		return faker.name().firstName().replace(".", "");
	}

	public static String randomLastName() {
		return faker.name().lastName().replace(".", "");
	}

	public static String randomTelephone() {
		return faker.phoneNumber().phoneNumberNational().replace("(", "").replace(")", "").replace(" ", "").replace("-", "");
	}
	
	public static String randomCellPhone() {
		return faker.phoneNumber().cellPhone().replace("(", "").replace(")", "").replace(" ", "").replace("-", "");
	}

	public static String randomInt(int minimum, int maximum) {
		return String.valueOf(faker.random().nextInt(minimum, maximum));
	}

	public static String randomDouble(double minimum, double maximum) {
		return String.valueOf(faker.random().nextDouble(minimum, maximum));
	}

    public static String randomDate() {
        LocalDate minimum = LocalDate.of(1970, 1, 1);
        LocalDate maximum = minimum.plusDays(1);

        return faker.timeAndDate().between(minimum.atStartOfDay().toInstant(ZoneOffset.UTC), maximum.atStartOfDay().toInstant(ZoneOffset.UTC), "yyyy-MM-dd");
    }

    public static String randomPastDate() {
        return faker.timeAndDate().past(
                1,
                TimeUnit.DAYS,
                "yyyy-MM-dd"
        );
    }

    public static String randomFutureDate() {
        return faker.timeAndDate().future(
                1,
                TimeUnit.DAYS,
                "yyyy-MM-dd"
        );
    }

    public static String randomBirthday() {
        return faker.timeAndDate().birthday(18, 68, "yyyy-MM-dd");
    }

    public static String randomCPF() {
        return faker.cpf().valid(false);
    }

    public static String randomCNPJ() {
        return faker.cnpj().valid(false);
    }

}
