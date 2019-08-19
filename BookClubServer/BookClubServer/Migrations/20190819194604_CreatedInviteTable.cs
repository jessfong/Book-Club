using Microsoft.EntityFrameworkCore.Metadata;
using Microsoft.EntityFrameworkCore.Migrations;

namespace BookClubServer.Migrations
{
    public partial class CreatedInviteTable : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Invites",
                columns: table => new
                {
                    ID = table.Column<int>(nullable: false)
                        .Annotation("SqlServer:ValueGenerationStrategy", SqlServerValueGenerationStrategy.IdentityColumn),
                    SenderId = table.Column<int>(nullable: true),
                    RecieverId = table.Column<int>(nullable: true),
                    BookClubId = table.Column<int>(nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Invites", x => x.ID);
                    table.ForeignKey(
                        name: "FK_Invites_BookClubs_BookClubId",
                        column: x => x.BookClubId,
                        principalTable: "BookClubs",
                        principalColumn: "ID",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_Invites_Users_RecieverId",
                        column: x => x.RecieverId,
                        principalTable: "Users",
                        principalColumn: "ID",
                        onDelete: ReferentialAction.Restrict);
                    table.ForeignKey(
                        name: "FK_Invites_Users_SenderId",
                        column: x => x.SenderId,
                        principalTable: "Users",
                        principalColumn: "ID",
                        onDelete: ReferentialAction.Restrict);
                });

            migrationBuilder.CreateIndex(
                name: "IX_Invites_BookClubId",
                table: "Invites",
                column: "BookClubId");

            migrationBuilder.CreateIndex(
                name: "IX_Invites_RecieverId",
                table: "Invites",
                column: "RecieverId");

            migrationBuilder.CreateIndex(
                name: "IX_Invites_SenderId",
                table: "Invites",
                column: "SenderId");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "Invites");
        }
    }
}
