       IDENTIFICATION DIVISION.
       PROGRAM-ID. NO-BRANCH.
       DATA DIVISION.
           WORKING-STORAGE SECTION.
                01 SOME-PART-1 PIC 999.
                01 SOME-PART-2 PIC 999.
                01 EXCHANGE-PART-01 PIC XXXX.
           LINKAGE SECTION.
               01  SOMETHING-LINKAGE      PIC XXXX.
       PROCEDURE DIVISION.
       SECTION-0 SECTION.
        P1.
            DISPLAY "GOING " SOME-PART-1 " AND " SOME-PART-2
       SECTION-A SECTION.
        P2.
            MOVE SOME-PART-1 TO SOME-PART-1.

