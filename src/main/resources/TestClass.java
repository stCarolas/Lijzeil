package ru.ds.rgf.efgi.accountingobject.core.business.accounting.creating;

import static io.vavr.API.For;
import static io.vavr.API.Option;
import static ru.ds.UserFriendlyException.FailForNone;

import java.util.function.Function;

import javax.inject.Named;

import io.vavr.Function2;
import io.vavr.control.Option;
import io.vavr.control.Try;
import ru.ds.rgf.efgi.accountingobject.core.business.accounting.ExistingAccountingObject;
import ru.ds.rgf.efgi.accountingobject.core.business.proxy.ReferencedObject;
import ru.ds.rgf.efgi.accountingobject.core.service.ao.AccountingObject;
import ru.ds.rgf.efgi.accountingobject.jpa.entity.ao.version.AccObjVer;

@Named("CreateAccountingObject")
public class TestClass 
	implements Function<AccountingObject, Try<ExistingAccountingObject>> {

	@Named("OptionalCreateReferencedObject")
	private Function<AccountingObject, Option<Try<ReferencedObject>>> 
		createRefencedObject;
	
	@Named("CreateAccountingObjectSnapshotWithReferencedObject")
	private Function2<Try<AccountingObject>, Option<Try<ReferencedObject>>, Try<AccObjVer>> 
		makeSnapshot;

	@Override
	public Try<ExistingAccountingObject> apply(
			AccountingObject accountingObject
	){
    Try<AccountingObject> accounting = Option(accountingObject)
      .map($ -> $)
      .toTry(FailForNone("Missing AccountingObject to create"));

		Option<Try<ReferencedObject>> referencedObject = 
			createRefencedObject.apply(accountingObject);

		Try<AccObjVer> snapshot = 
			makeSnapshot.apply(accounting, referencedObject);

		return For(snapshot, accountingWithDetails).yield(ExistingAccountingObject::new);
	}

}
