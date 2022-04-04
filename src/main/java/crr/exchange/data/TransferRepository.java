package crr.exchange.data;

import crr.exchange.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer,Long> {
}
