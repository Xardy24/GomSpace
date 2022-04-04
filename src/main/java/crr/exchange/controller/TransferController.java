package crr.exchange.controller;

import com.github.sarxos.xchange.ExchangeException;
import lombok.RequiredArgsConstructor;
import crr.exchange.model.Transfer;
import crr.exchange.service.TransferServer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TransferController {

    private final TransferServer transferServer;

    @PostMapping("/transfers")
    ResponseEntity<?> newTransfer(@RequestBody Transfer transfer) throws ExchangeException {
        Transfer newTransfer = transferServer.doTransfer(transfer);
        return ResponseEntity.ok(newTransfer);
    }
}
