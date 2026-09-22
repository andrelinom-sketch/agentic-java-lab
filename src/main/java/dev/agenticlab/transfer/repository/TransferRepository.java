package dev.agenticlab.transfer.repository;

import dev.agenticlab.transfer.model.Transfer;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, UUID> {}
