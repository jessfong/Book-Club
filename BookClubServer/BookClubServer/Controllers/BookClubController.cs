﻿using System.Net;
using System.Threading.Tasks;
using BookClubServer.Data;
using BookClubServer.Models;
using BookClubServer.Services;
using Microsoft.AspNetCore.Mvc;

namespace BookClubServer.Controllers
{
    [Route("api/[controller]/[action]")]
    [ApiController]
    public class BookClubController : ControllerBase
    {
        private readonly IBookClubServices _bookClubServices;

        public BookClubController(IBookClubServices bookClubServices)
        {
            _bookClubServices = bookClubServices;
        }

        /// <summary>
        /// Creates new user if user doesn't already exists and if password is valid
        /// </summary>
        /// <param name="userCreateModel"> User data to create account with </param>
        /// <returns> New user or error message </returns>
        [HttpPost]
        public async Task<IActionResult> RegisterNewUser([FromBody] UserCreateModel userCreateModel)
        {
            if (userCreateModel.Email.Equals("")|| userCreateModel.Password.Equals(""))
            {
                Response.StatusCode = (int)HttpStatusCode.BadRequest;
                return new JsonResult("Email and password must not be empty.");
            }

            if (!_bookClubServices.IsValidEmail(userCreateModel.Email))
            {
                Response.StatusCode = (int)HttpStatusCode.BadRequest;
                return new JsonResult($"{userCreateModel.Email} is not a valid email.");
            }

            if (_bookClubServices.DoesUserExist(userCreateModel.Email))
            {
                Response.StatusCode = (int)HttpStatusCode.BadRequest;
                return new JsonResult($"User with the email {userCreateModel.Email} already exists.");
            }

            if (!_bookClubServices.IsStrongPassword(userCreateModel.Password))
            {
                Response.StatusCode = (int)HttpStatusCode.BadRequest;
                return new JsonResult($"Passwords must include at least one uppercase letter, " +
                    $"one lowercase letter, one number, and a non-alphanumeric character.");
            }

            return new JsonResult(await _bookClubServices.RegisterNewUserAsync(userCreateModel));
        }

        /// <summary>
        /// Checks if username and password are valid for login
        /// </summary>
        /// <param name="user"> User to look for </param>
        /// <returns> If user is valid or not </returns>
        [HttpPost]
        public IActionResult SignIn([FromBody] User user)
        {
            var result = _bookClubServices.SignIn(user);

            if (result == null)
            {
                Response.StatusCode = (int)HttpStatusCode.NotFound;
                return new JsonResult("Incorrect email or password.");
            }

            return Ok();
        }

        /// <summary>
        /// Creates a new book club after checking if user is signed in
        /// </summary>
        /// <param name="bookClubCreateModel"> Data to create book club with </param>
        /// <returns> A new book club </returns>
        public async Task<IActionResult> CreateBookClub(BookClubCreateModel bookClubCreateModel)
        {
            var result = _bookClubServices.SignIn(bookClubCreateModel.GetUser());
            if (result == null)
            {
                Response.StatusCode = (int)HttpStatusCode.NotFound;
                return new JsonResult("Username or password is invalid.");
            }

            bookClubCreateModel.AdminId = result.ID;

            return new JsonResult(await _bookClubServices.CreateBookClubAsync(bookClubCreateModel));
        }

        /// <summary>
        /// Checks if user is signed in then deletes specified book club
        /// </summary>
        /// <param name="bookClub"> Book club to be deleted </param>
        /// <returns> If the book club was deleted or not </returns>
        public async Task<IActionResult> DeleteBookClub(DeleteBookClubModel deleteBookClubModel)
        {
            // TODO: 
            var user = await _bookClubServices.RetrieveUser(deleteBookClubModel.Email);

            var result = _bookClubServices.SignIn(user);
            if (result == null)
            {
                Response.StatusCode = (int)HttpStatusCode.NotFound;
                return new JsonResult("User is not logged in.");
            }

            var clubDeleted = await _bookClubServices.DeleteBookClubAsync(bookClub);

            switch (clubDeleted)
            {
                case -1:
                    return new JsonResult($"The book club with name {bookClub.Name}, could not be found.");
                case 0:
                    return Ok();
                case 1:
                    return new JsonResult($"There was an error deleting {bookClub.Name}.");
            }

            return new JsonResult($"There was an error deleting {bookClub.Name}.");
        }

        /// <summary>
        /// Checks if user sending invite is signed in and if both users are signed in
        /// If both users exist and sender is signed in then an invite is created
        /// </summary>
        /// <param name="inviteCreateModel"> Invite to create </param>
        /// <returns> A new invite </returns>
        public async Task<IActionResult> CreateInvite(InviteCreateModel inviteCreateModel)
        {
            var sender = await _bookClubServices.RetrieveUser(inviteCreateModel.SenderId);

            var result = _bookClubServices.SignIn(sender);
            if (result == null)
            {
                Response.StatusCode = (int)HttpStatusCode.NotFound;
                return new JsonResult("User sending invite was not found.");
            }            
            
            if (sender == null)
            {
                Response.StatusCode = (int)HttpStatusCode.NotFound;
                return new JsonResult("User sending invite was not found.");
            }

            var reciever = await _bookClubServices.RetrieveUser(inviteCreateModel.RecieverId);
            if (reciever == null)
            {
                Response.StatusCode = (int)HttpStatusCode.NotFound;
                return new JsonResult("User recieving invite was not found.");
            }

            if (sender.Email == reciever.Email)
            {
                Response.StatusCode = (int)HttpStatusCode.NotFound;
                return new JsonResult("Sender and reciever can't be the same.");
            }

            if (!_bookClubServices.DoesBookClubExist(inviteCreateModel.BookClubId))
            {
                Response.StatusCode = (int)HttpStatusCode.NotFound;
                return new JsonResult("Book club was not found.");
            }

            var userIsAdmin = _bookClubServices.IsBookClubAdmin(inviteCreateModel);
            if (!userIsAdmin)
            {
                Response.StatusCode = (int)HttpStatusCode.NotFound;
                return new JsonResult("User is not authorized to invite other users.");
            }

            var existingInvite = new ExistingInviteModel
            {
                SenderId = inviteCreateModel.SenderId,
                RecieverId = inviteCreateModel.RecieverId,
                BookClubId = inviteCreateModel.BookClubId
            };

            var inviteExists = _bookClubServices.InviteExists(existingInvite);
            if (inviteExists)
            {
                Response.StatusCode = (int)HttpStatusCode.NotFound;
                return new JsonResult("Invite already exists.");
            }

            return new JsonResult(await _bookClubServices.CreateInviteAsync(inviteCreateModel));
        }

        public async Task<IActionResult> AcceptInvite(AcceptInviteModel acceptInviteModel)
        {
            var result = _bookClubServices.SignIn(acceptInviteModel.GetUser());
            if (result == null)
            {
                Response.StatusCode = (int)HttpStatusCode.NotFound;
                return new JsonResult("Username or password is invalid.");
            }

            var existingInvite = new ExistingInviteModel
            {
                InviteId = acceptInviteModel.InviteId
            };

            var inviteExists = _bookClubServices.InviteExists(existingInvite);
            if (!inviteExists)
            {
                Response.StatusCode = (int)HttpStatusCode.NotFound;
                return new JsonResult("Invite does not exist.");
            }

            var alreadyAccepted = _bookClubServices.userAlreadyMember(acceptInviteModel);
            if (alreadyAccepted)
            {
                Response.StatusCode = (int)HttpStatusCode.NotFound;
                return new JsonResult("User is already a member of this book club.");
            }

            var acceptInvite = await _bookClubServices.AcceptInviteAsync(acceptInviteModel);
            if (acceptInvite)
            {
                return Ok();
            }

            return new JsonResult("User could not be found.");
        }
    }
}