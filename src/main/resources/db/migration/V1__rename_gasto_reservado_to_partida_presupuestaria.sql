-- Renombra la tabla de reservas hacia partidas presupuestarias preservando la información existente.
SET @table_exists := (
    SELECT COUNT(*)
    FROM information_schema.TABLES
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'gasto_reservado'
);

SET @rename_sql := IF(@table_exists > 0,
    'RENAME TABLE gasto_reservado TO partida_presupuestaria;',
    'DO 0;'
);
PREPARE stmt FROM @rename_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @new_table_exists := (
    SELECT COUNT(*)
    FROM information_schema.TABLES
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'partida_presupuestaria'
);

-- Actualiza los nombres de las claves foráneas sólo si existen con la nomenclatura anterior.
SET @fk_categoria_exists := IF(@new_table_exists > 0,
    (
        SELECT COUNT(*)
        FROM information_schema.TABLE_CONSTRAINTS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'partida_presupuestaria'
          AND CONSTRAINT_NAME = 'fk_reserva_categoria'
    ),
    0
);
SET @drop_fk_categoria := IF(@fk_categoria_exists > 0,
    'ALTER TABLE partida_presupuestaria DROP FOREIGN KEY fk_reserva_categoria;',
    'DO 0;'
);
PREPARE stmt FROM @drop_fk_categoria;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk_periodo_exists := IF(@new_table_exists > 0,
    (
        SELECT COUNT(*)
        FROM information_schema.TABLE_CONSTRAINTS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'partida_presupuestaria'
          AND CONSTRAINT_NAME = 'fk_reserva_periodo'
    ),
    0
);
SET @drop_fk_periodo := IF(@fk_periodo_exists > 0,
    'ALTER TABLE partida_presupuestaria DROP FOREIGN KEY fk_reserva_periodo;',
    'DO 0;'
);
PREPARE stmt FROM @drop_fk_periodo;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Crea las nuevas claves foráneas únicamente si la tabla existe y aún no están definidas.
SET @new_fk_categoria_missing := IF(@new_table_exists > 0,
    (
        SELECT COUNT(*)
        FROM information_schema.TABLE_CONSTRAINTS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'partida_presupuestaria'
          AND CONSTRAINT_NAME = 'fk_partida_categoria'
    ),
    -1
);
SET @add_fk_categoria := IF(@new_fk_categoria_missing = 0,
    'ALTER TABLE partida_presupuestaria ADD CONSTRAINT fk_partida_categoria FOREIGN KEY (categoria_id) REFERENCES categoria_financiera (id);',
    'DO 0;'
);
PREPARE stmt FROM @add_fk_categoria;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @new_fk_periodo_missing := IF(@new_table_exists > 0,
    (
        SELECT COUNT(*)
        FROM information_schema.TABLE_CONSTRAINTS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'partida_presupuestaria'
          AND CONSTRAINT_NAME = 'fk_partida_periodo'
    ),
    -1
);
SET @add_fk_periodo := IF(@new_fk_periodo_missing = 0,
    'ALTER TABLE partida_presupuestaria ADD CONSTRAINT fk_partida_periodo FOREIGN KEY (periodo_id) REFERENCES periodo_financiero (id);',
    'DO 0;'
);
PREPARE stmt FROM @add_fk_periodo;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
