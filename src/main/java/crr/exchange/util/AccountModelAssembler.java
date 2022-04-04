package crr.exchange.util;

import crr.exchange.controller.AccountController;
import crr.exchange.model.Account;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AccountModelAssembler implements RepresentationModelAssembler<Account, EntityModel<Account>> {

    @Override
    public EntityModel<Account> toModel(Account account) {
        return EntityModel.of(
                account,
                linkTo(methodOn(AccountController.class).oneAccount(account.getId())).withSelfRel(),
                linkTo(methodOn(AccountController.class).allAccounts()).withRel("accounts")
        );
    }
}
