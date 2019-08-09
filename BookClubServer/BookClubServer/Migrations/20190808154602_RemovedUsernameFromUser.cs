using Microsoft.EntityFrameworkCore.Migrations;

namespace BookClubServer.Migrations
{
    public partial class RemovedUsernameFromUser : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "Username",
                table: "Users");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "Username",
                table: "Users",
                nullable: false,
                defaultValue: "");
        }
    }
}
