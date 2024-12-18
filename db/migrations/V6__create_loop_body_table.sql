create table LOOP_BODY (
                        ID integer primary key autoincrement,
                        CFG_ID integer,
                        BODY TEXT not null,
                        DATE_CREATED TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (CFG_ID) REFERENCES IR_CFG(ID)
);
