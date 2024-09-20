       IDENTIFICATION DIVISION.
       PROGRAM-ID.    FLOWGRAPH.
       AUTHOR.        MOJO
       DATE-WRITTEN.  SEP 2024.
       ENVIRONMENT DIVISION.
       DATA DIVISION.
       WORKING-STORAGE SECTION.
       01  CONDI                PIC X VALUE "E".
            88 FAILURE          VALUE "E".
            88 DB-STATUS-OK     VALUE "F".
            88 RECORD-NOT-FOUND VALUE "F".
            88 DB-READ-END    VALUE "F".
         01 S-ERROR             PIC XXXX.
         01 STATE-1             PIC 9999.

       PROCEDURE DIVISION.
       S SECTION.
       SA1.
           PERFORM A1.
           IF  NOT FAILURE
               GO TO SZ1.
       SE1.
           PERFORM E1.
       SZ1.
           PERFORM Z1.
       STATUS-CHECK SECTION.
       STATUS-CHECK-A.
           IF  DB-STATUS-OK
           OR  RECORD-NOT-FOUND
           OR  DB-READ-END
               NEXT SENTENCE
           ELSE
               PERFORM YES-ABORT
               PERFORM Z1.
       STATUS-Z.
           EXIT.
       YES-ABORT SECTION.
       YES-ABORT-A.
           DISPLAY 'ABORTA'
       ABORT-Z.
           EXIT.
       A1 SECTION.
       A1A.
        DISPLAY "A1A".
       A1Z.
           EXIT.
       E1 SECTION.
       E1A.
           DISPLAY "E1A"
           IF STATE-1 = ZERO
              GO TO E1Z.
       E1B.
           IF   DB-STATE = STATE-1
                DISPLAY "TEST5"
           ELSE
                GO TO E1B.
       E1Z.
           EXIT.
       Z1 SECTION.
       Z1A.
           FINISH
             ON ANY-STATUS
                 NEXT SENTENCE.
           GOBACK.
       Z1Z.
           EXIT.
